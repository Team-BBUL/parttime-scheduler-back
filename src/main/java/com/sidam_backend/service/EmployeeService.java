package com.sidam_backend.service;

import com.sidam_backend.data.Store;
import com.sidam_backend.data.UserRole;
import com.sidam_backend.repo.StoreRepository;
import com.sidam_backend.repo.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service // DB와 Controller 사이에서 실질적인 비즈니스 로직을 작업하는 역할
@RequiredArgsConstructor // final로 선언된 인스턴스를 받아오는 생성자를 자동으로 생성해줌
public class EmployeeService {

    private final UserRoleRepository userRoleRepository;
    private final StoreRepository storeRepository;

    public List<UserRole> getAllEmployees(Long storeId) {

        ArrayList<UserRole> users;

        Store store = storeRepository.findById(storeId)
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 매장입니다."));

        UserRole ex = new UserRole();
        ex.setStore(store);

        users = (ArrayList<UserRole>) userRoleRepository.findAll();

        return users;
    }

    public UserRole getEmployee(String storeId, String userId) {

        UserRole user = new UserRole();

        // DB에서 특정 user가져오기

        user.setId(userId);
        user.setAlias("아무개");
        user.setCost(9700);
        user.setValid(true);
        user.setSalary(true);
        user.setLevel(1);
        user.setColor("0x000000");

        return user;
    }
}
