package com.sidam_backend.service;

import com.sidam_backend.data.*;
import com.sidam_backend.data.enums.Role;
import com.sidam_backend.repo.*;

import com.sidam_backend.resources.ColorSet;
import com.sidam_backend.resources.MinimumWages;
//import com.sidam_backend.service.base.UsingAlarmService;
import com.sidam_backend.service.base.UsingAlarmService;
import com.sidam_backend.service.base.Validation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service // DB와 Controller 사이에서 실질적인 비즈니스 로직을 작업하는 역할
public class EmployeeService extends UsingAlarmService implements Validation  {

    public EmployeeService(
            AccountRoleRepository accountRoleRepository,
            StoreRepository storeRepository,
            AccountRepository accountRepository,
            AlarmRepository alarmRepository,
            AlarmReceiverRepository receiverRepository
    ) {
        super(alarmRepository, accountRoleRepository, receiverRepository);

        this.accountRepository = accountRepository;
        this.storeRepository = storeRepository;
        this.accountRoleRepository = accountRoleRepository;
    }

    private final AccountRoleRepository accountRoleRepository;
    private final StoreRepository storeRepository;
    private final AccountRepository accountRepository;

    private MinimumWages wages;

    public Store validateStoreId(Long storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException(storeId + " store is not exist."));
    }
    public AccountRole validateRoleId(Long roleId) {
        return accountRoleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException(roleId + " role is not exist."));
    }
    public Account validateAccount(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException(accountId + " account is not exist."));
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
        Account account = accountRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException(accountRole + " user is not exist."));

        // account 및 store 정보 set
        accountRole.setAccount(account);
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

        accountRole.setAlias(account.getName());

        log.info("add: " + accountRole);

        accountRoleRepository.save(accountRole);

        return getEmployee(store, accountRole.getId());
    }

    public AccountRole getEmployee(Store store, Long roleId) {

        return accountRoleRepository.findByIdAndStore(roleId, store)
                .orElseThrow(() -> new IllegalArgumentException(roleId + " role is not exist."));
    }

    public AccountRole putEmployee(Store store, Long roleId, AccountRole editRole) {

        AccountRole oldRole = accountRoleRepository.findById(roleId)
                .orElseThrow(()-> new IllegalArgumentException(roleId + " user is not exist."));

        editRole.setAccount(oldRole.getAccount());
        editRole.setStore(oldRole.getStore());
        editRole.setColor(oldRole.getColor());
        editRole.setId(oldRole.getId());

        accountRoleRepository.save(editRole);

        return getEmployee(store, editRole.getId());
    }

    public void deleteEmployee(Store store, Long roleId) {

        AccountRole accountRole = accountRoleRepository.findByIdAndStore(roleId, store)
                .orElseThrow(() -> new IllegalArgumentException(roleId + " userRole is not exist."));

        accountRoleRepository.delete(accountRole);
    }

    public boolean checkIfUserHasRole(Long accountId, Long roleId){

        AccountRole accountRole = accountRoleRepository.findById(roleId).
                orElseThrow(() -> new IllegalArgumentException(roleId + " AccountRole is not exist."));
        return accountId.equals(accountRole.getAccount().getId());
    }

    public AccountRole getEmployeeByAccountId(Store store, Long accountId) {
        return accountRoleRepository
                .findByAccountIdAndStore(accountId, store)
                .orElseThrow(() -> new IllegalArgumentException("AccountRole is not exist"));
    }

    public AccountRole getAccountRoleWithStore(Store store, Long id){
        return accountRoleRepository
                .findWithStoreByAccountIdAndStore(id, store);
    }

    private void checkIfManager(Long accountId, Store store){
        AccountRole accountRole = accountRoleRepository
                .findByAccountIdAndStore(accountId, store)
                .orElseThrow(() -> new IllegalArgumentException("AccountRole is not exist."));

        if(!accountRole.isManager()){
            throw new AccessDeniedException("No Authority");
        }
    }
}
