package com.sidam_backend;

import com.sidam_backend.data.UserRole;
import com.sidam_backend.repo.UserRoleRepository;
import jakarta.transaction.Transactional;
import com.sidam_backend.data.User;
import com.sidam_backend.repo.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;

@Transactional
@SpringBootTest
public class UserRoleRepoTest {
    @Autowired
    private UserRoleRepository userRoleRepository;

    @DisplayName("유저롤 객체 db 등록")
    @Test
    public void registerUserRoleEntity(){

        //user 생성
        User user = new User();
        user.setKakaoId("asdf");
        user.setName("홍길동");
        user.setEmail("asdf@gmail.com");
        user.setPhone("010-1234-5678");
        user.setProfile("이거무슨필드더라");
        user.setDevice("기기 original code");

        UserRole userRole = UserRole.builder()
                .userRoleId("1234")
                .alias("홍길동")
                .level(2)
                .cost(10000)
                .color("#0000FF")
                .isSalary(false)
                .valid(true)
                .userKakaoId(user)
                .build();

        UserRole newUserRole = userRoleRepository.save(userRole);

        UserRole result = userRoleRepository.findByUserRoleId("1234");

    }

}
