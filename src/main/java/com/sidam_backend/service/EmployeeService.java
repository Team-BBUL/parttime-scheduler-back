package com.sidam_backend.service;

import com.sidam_backend.data.*;
import com.sidam_backend.data.enums.Role;
import com.sidam_backend.repo.*;

import com.sidam_backend.resources.ColorSet;
import com.sidam_backend.resources.DTO.PostEmployee;
import com.sidam_backend.resources.DTO.UpdateAccount;
import com.sidam_backend.resources.MinimumWages;
import com.sidam_backend.resources.UpdateAuth;
import com.sidam_backend.service.base.UsingAlarmService;
import com.sidam_backend.service.base.Validation;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service // DB와 Controller 사이에서 실질적인 비즈니스 로직을 작업하는 역할
public class EmployeeService extends UsingAlarmService implements Validation {

    public EmployeeService(
            AccountRoleRepository accountRoleRepository,
            StoreRepository storeRepository,
            AlarmRepository alarmRepository,
            AlarmReceiverRepository receiverRepository,
            PasswordEncoder passwordEncoder
    ) {
        super(alarmRepository, accountRoleRepository, receiverRepository);
        this.storeRepository = storeRepository;
        this.accountRoleRepository = accountRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }
    private final AccountRoleRepository accountRoleRepository;
    private final StoreRepository storeRepository;
    private final PasswordEncoder passwordEncoder;

    private MinimumWages wages;
    private ColorSet colorSet;

    public Store validateStoreId(Long storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException(storeId + " store is not exist."));
    }
    public AccountRole validateRoleId(Long roleId) {
        return accountRoleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException(roleId + " role is not exist."));
    }

    @Override
    public DailySchedule validateSchedule(Long scheduleId) {
        return null;
    }

    public List<AccountRole> getAllEmployees(Store store) {
//        this.checkIfManager(accountId,store);

        ArrayList<AccountRole> users;


        AccountRole ex = new AccountRole();
        ex.setStore(store);

        users = (ArrayList<AccountRole>) accountRoleRepository.findByStore(store)
                .orElseThrow(() -> new IllegalArgumentException(store.getId() + " store is not exist."));

        for(AccountRole u : users) {
            log.info("GET users : " + u.getAlias() + "/" + u.getId());
        }

        return users;
    }

    public AccountRole postEmployee(Store store, String userId) {

        AccountRole accountRole = new AccountRole();

        // account id로 검색
//        Account account = accountRepository.findById(userId)
//                .orElseThrow(()-> new IllegalArgumentException(accountRole + " user is not exist."));

        // account 및 store 정보 set
//        accountRole.setAccount(account);
        accountRole.setStore(store);

        wages = new MinimumWages();
        if (accountRole.getCost() < wages.getWages()) {
            accountRole.setCost(wages.getWages());
        }

        // color 셋팅
        ColorSet colors = new ColorSet(store.getIdx());
        accountRole.setColor(colors.getColor());
        store.setIdx(store.getIdx() + 1);
        storeRepository.save(store);

        // isSalary 판단
        accountRole.setSalary(true);

        // level = 1?
        accountRole.setLevel(1);

//        accountRole.setAlias(account.getName());

        log.info("add: " + accountRole);

        accountRoleRepository.save(accountRole);

        return getMyInfo(store, accountRole.getId());
    }

    public AccountRole getMyInfo(Store store, Long roleId) {

        return accountRoleRepository.findByIdAndStore(roleId, store)
                .orElseThrow(() -> new IllegalArgumentException(roleId + " role is not exist."));
    }

    @Transactional
    public AccountRole putEmployee(Store store, Long roleId, UpdateAccount editRole) {

        AccountRole oldRole = accountRoleRepository.findById(roleId)
                .orElseThrow(()-> new IllegalArgumentException(roleId + " user is not exist."));

        oldRole.setAlias(editRole.getAlias());
        oldRole.setColor(editRole.getColor());
        oldRole.setCost(editRole.getCost());
        oldRole.setLevel(editRole.getLevel());

        return getMyInfo(store, oldRole.getId());
    }

    public void deleteEmployee(Store store, Long roleId) {

        AccountRole accountRole = accountRoleRepository.findByIdAndStore(roleId, store)
                .orElseThrow(() -> new IllegalArgumentException(roleId + " userRole is not exist."));

        accountRoleRepository.delete(accountRole);
    }

    public AccountRole getMyInfo(String accountId){

        return accountRoleRepository.findByAccountId(accountId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 아이디가 없습니다."));
    }

    public AccountRole getEmployeeByAccountId(Store store, String id) {
        return accountRoleRepository.findByIdAndStore(Long.valueOf(id),store)
                .orElseThrow(() -> new IllegalArgumentException(" AccountRole is not exist."));
    }

    public AccountRole processNewEmployee(AccountRole owner, PostEmployee postEmployee, Store store) {
        log.info("store idx = {}", store.getIdx());
        colorSet = new ColorSet(store.getIdx());

        AccountRole employee  = new AccountRole();

        employee.setAccountId(postEmployee.getAccountId());
        employee.setPassword(passwordEncoder.encode(postEmployee.getPassword()));
        employee.setOriginAccountId(postEmployee.getAccountId());
        employee.setOriginPassword(postEmployee.getPassword());
        employee.setAlias(postEmployee.getAlias());
        employee.setSalary(postEmployee.isSalary());
        employee.setLevel(postEmployee.getLevel());
        employee.setCost(postEmployee.getCost());
        employee.setStore(owner.getStore());
        employee.setJoinedAt(LocalDateTime.now());
        employee.setRole(Role.EMPLOYEE);
        employee.setStore(owner.getStore());
        employee.setColor(colorSet.getColor());
        increaseIdx(store);

        return accountRoleRepository.save(employee);
    }

    @Transactional
    public AccountRole updateAccount(UpdateAuth updateAuth, AccountRole myInfo) {

        myInfo.setAccountId(updateAuth.getAccountId());
        myInfo.setPassword(passwordEncoder.encode(updateAuth.getPassword()));
        myInfo.setValid(true);
        return myInfo;
    }

    @Transactional
    public void clearAuth(AccountRole employee) {
        employee.setAccountId(employee.getOriginAccountId());
        employee.setPassword(employee.getOriginPassword());
        employee.setValid(false);
    }
//    public boolean checkIfUserHasRole(Long accountId, Long roleId){
//
//        AccountRole accountRole = accountRoleRepository.findById(roleId).
//                orElseThrow(() -> new IllegalArgumentException(roleId + " AccountRole is not exist."));
//        return accountId.equals(accountRole.getAccount().getId());
//    }

    @Transactional
    private void increaseIdx(Store store) {
        store.setIdx(store.getIdx() + 1);
    }
}
