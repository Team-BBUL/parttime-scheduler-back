package com.sidam_backend.user;

import com.sidam_backend.data.User;
import com.sidam_backend.repo.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
public class UserRepoTest {
    @Autowired
    private UserRepository userRepository;

    @DisplayName("유저 객체 db 등록")
    @Test
    void registerUserEntity(){
        User user = User.builder()
                .kakaoId("asdf")
                .name("홍길동")
                .email("asdf@gmail.com")
                .phone("010-1234-5678")
                .profile("아마카카오로그인api통해서가져올수있는프로필정보일거에요")
                .device("originalcode")
                .build();
        User newUser = userRepository.save(user);

        User result = userRepository.findByName("홍길동");

        Assertions.assertThat(result).isEqualTo(newUser);
    }

}
