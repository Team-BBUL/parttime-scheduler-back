package com.sidam_backend.service;

import com.sidam_backend.data.Store;
import com.sidam_backend.data.User;
import com.sidam_backend.data.UserRole;
import com.sidam_backend.repo.StoreRepository;
import com.sidam_backend.repo.UserRepository;
import com.sidam_backend.repo.UserRoleRepository;

import com.sidam_backend.resources.ColorSet;
import com.sidam_backend.resources.MinimumWages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service // DB와 Controller 사이에서 실질적인 비즈니스 로직을 작업하는 역할
@RequiredArgsConstructor // final로 선언된 인스턴스를 받아오는 생성자를 자동으로 생성해줌
public class EmployeeService {

    private final UserRoleRepository userRoleRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    private MinimumWages wages;

    public List<UserRole> getAllEmployees(Long storeId) {

        ArrayList<UserRole> users;

        Store store = storeRepository.findById(storeId)
                .orElseThrow(()-> new IllegalArgumentException(storeId + "은 존재하지 않는 매장입니다."));

        UserRole ex = new UserRole();
        ex.setStore(store);

        users = (ArrayList<UserRole>) userRoleRepository.findByStore(store);

        for(UserRole u : users) {
            log.info("GET users : " + u.getAlias() + "/" + u.getId());
        }

        return users;
    }

    public UserRole postEmployee(Long storeId, String userId) {

        UserRole userRole = new UserRole();

        // store id로 검색해서 store 객체 저장 및 userRole 객체에 set
        Store store = storeRepository.findById(storeId)
                .orElseThrow(()-> new IllegalArgumentException(storeId + "은 존재하지 않는 매장입니다."));
        userRole.setStore(store);

        // user id로 검색해서 user 객체 저장 및 userRole 객체에 set
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException(userRole + "은 존재하지 않는 유저입니다."));
        userRole.setUser(user);

        userRole.setId(null);

        wages = new MinimumWages();
        if (userRole.getCost() < wages.getWages()) {
            userRole.setCost(wages.getWages());
        }

        // color 어떻게 셋팅하지...?
        ColorSet colors = ColorSet.getInstance();
        userRole.setColor(colors.getColor());

        // isSalary 판단
        userRole.setSalary(true);

        // level = 1?
        userRole.setLevel(1);

        userRole.setAlias(user.getName());

        log.info("add: " + userRole);

        userRoleRepository.save(userRole);

        // 점주에게 매장 가입 요청 알림 전송

        return getEmployee(storeId, userRole.getId());
    }

    public UserRole getEmployee(Long storeId, Long roleId) {

        Store store = storeRepository.findById(storeId)
                .orElseThrow(()-> new IllegalArgumentException(storeId + "은 존재하지 않는 매장입니다."));

        return userRoleRepository.findByIdAndStore(roleId, store);
    }

    public UserRole putEmployee(Long storeId, Long roleId, UserRole editRole) {

        UserRole oldRole = userRoleRepository.findById(roleId)
                .orElseThrow(()-> new IllegalArgumentException(roleId + "은 존재하지 않는 사용자입니다."));

        editRole.setUser(oldRole.getUser());
        editRole.setStore(oldRole.getStore());
        editRole.setColor(oldRole.getColor());
        editRole.setId(oldRole.getId());

        userRoleRepository.save(editRole);

        return getEmployee(storeId, editRole.getId());
    }

    public void deleteEmployee(Long storeId, Long roleId) {

        Store store = storeRepository.findById(storeId)
                .orElseThrow(()-> new IllegalArgumentException(storeId + "은 존재하지 않는 매장입니다."));

        UserRole userRole = userRoleRepository.findByIdAndStore(roleId, store);

        userRoleRepository.delete(userRole);
    }
}
