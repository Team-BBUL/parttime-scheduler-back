package com.sidam_backend.service;

import com.sidam_backend.data.Account;
import com.sidam_backend.data.Store;
import com.sidam_backend.data.UserRole;
import com.sidam_backend.repo.StoreRepository;
import com.sidam_backend.repo.UserRepository;
import com.sidam_backend.repo.UserRoleRepository;

import com.sidam_backend.resources.ColorSet;
import com.sidam_backend.resources.MinimumWages;
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
                .orElseThrow(()-> new IllegalArgumentException(storeId + " store is not exist."));

        UserRole ex = new UserRole();
        ex.setStore(store);

        users = (ArrayList<UserRole>) userRoleRepository.findByStore(store)
                .orElseThrow(() -> new IllegalArgumentException(store.getId() + " store is not exist."));

        for(UserRole u : users) {
            log.info("GET users : " + u.getAlias() + "/" + u.getId());
        }

        return users;
    }

    public UserRole postEmployee(Long storeId, String userId) {

        UserRole userRole = new UserRole();

        // store id로 검색해서 store 객체 저장 및 userRole 객체에 set
        Store store = storeRepository.findById(storeId)
                .orElseThrow(()-> new IllegalArgumentException(storeId + " store is not exist."));
        userRole.setStore(store);

        // user id로 검색해서 user 객체 저장 및 userRole 객체에 set
        Account account = userRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException(userRole + " user is not exist."));
        userRole.setAccount(account);

        userRole.setId(null);

        wages = new MinimumWages();
        if (userRole.getCost() < wages.getWages()) {
            userRole.setCost(wages.getWages());
        }

        // color 셋팅
        ColorSet colors = new ColorSet(store.getIdx());
        userRole.setColor(colors.getColor());
        store.setIdx(store.getIdx() + 1);
        storeRepository.save(store);

        // isSalary 판단
        userRole.setSalary(true);

        // level = 1?
        userRole.setLevel(1);

        userRole.setAlias(account.getName());

        log.info("add: " + userRole);

        userRoleRepository.save(userRole);

        // 점주에게 매장 가입 요청 알림 전송

        return getEmployee(storeId, userRole.getId());
    }

    public UserRole getEmployee(Long storeId, Long roleId) {

        Store store = storeRepository.findById(storeId)
                .orElseThrow(()-> new IllegalArgumentException(storeId + " store is not exist."));

        return userRoleRepository.findByIdAndStore(roleId, store)
                .orElseThrow(() -> new IllegalArgumentException(roleId + " userRole is not exist."));
    }

    public UserRole putEmployee(Long storeId, Long roleId, UserRole editRole) {

        UserRole oldRole = userRoleRepository.findById(roleId)
                .orElseThrow(()-> new IllegalArgumentException(roleId + " user is not exist."));

        editRole.setAccount(oldRole.getAccount());
        editRole.setStore(oldRole.getStore());
        editRole.setColor(oldRole.getColor());
        editRole.setId(oldRole.getId());

        userRoleRepository.save(editRole);

        return getEmployee(storeId, editRole.getId());
    }

    public void deleteEmployee(Long storeId, Long roleId) {

        Store store = storeRepository.findById(storeId)
                .orElseThrow(()-> new IllegalArgumentException(storeId + " store is not exist."));

        UserRole userRole = userRoleRepository.findByIdAndStore(roleId, store)
                .orElseThrow(() -> new IllegalArgumentException(roleId + " userRole is not exist."));

        userRoleRepository.delete(userRole);
    }
}
