package com.sidam_backend.service;

import com.sidam_backend.data.AccountRole;
import com.sidam_backend.data.Store;
import com.sidam_backend.repo.AccountRoleRepository;
import com.sidam_backend.repo.StoreRepository;
import com.sidam_backend.resources.DTO.StoreForm;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final AccountRoleRepository accountRoleRepository;

    public Store createNewStore(StoreForm storeForm) {
        Store store = new Store();
        store.setName(storeForm.getName());
        store.setIdx(0);
        store.setLocation(storeForm.getLocation());
        store.setPhone(storeForm.getPhone());
        store.setOpen(storeForm.getOpen());
        store.setClosed(storeForm.getClosed());
        store.setPayday(storeForm.getPayday());
        store.setStartDayOfWeek(storeForm.getStartDayOfWeek());
        store.setDeadlineOfSubmit(storeForm.getDeadlineOfSubmit());
        Store newStore = storeRepository.save(store);
        log.info("newStore = {}", newStore);

        return newStore;
    }

//    public AccountRole createNewAccountRole(Store newStore, Long id, Role role) {
//
//
//        AccountRole newAccountRole = new AccountRole();
//        newAccountRole.setAlias();
//        newAccountRole.setLevel(0);
//        newAccountRole.setCost(1);
//        newAccountRole.setColor("white");
//        newAccountRole.setSalary(true);
//        newAccountRole.setValid(true);
//        newAccountRole.setStore(newStore);
//        newAccountRole.setAccount(account);
//        newAccountRole.setRole(role);
//
//        AccountRole accountRole = accountRoleRepository.save(newAccountRole);
//
//        log.info("newAccount = {}", accountRole);
//
//        return accountRole;
//    }

    public AccountRole findById(Long roleId) {
        return accountRoleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException(roleId + " is invalid."));
    }

    public Store findStoreById(Long storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(IllegalArgumentException::new);
    }

    public List<Store> findStore(String input) throws IllegalArgumentException {

        List<Store> stores = storeRepository.findAllByName(input)
                .orElseThrow(() -> new IllegalArgumentException(input + " is invalid."));

        if (stores.size() == 0) {
            throw new IllegalArgumentException("results not found.");
        }

        return stores;
    }

    public List<String> findAllStoreName() {

        return storeRepository.findNameAll();
    }

//    public List<Store> getMyStores(Long id, Role role) {
//        List<AccountRole> accountRoles = accountRoleRepository.
//                findAccountRolesByAccountIdAndRole(id, role)
//                .orElseThrow(() -> new IllegalArgumentException(id + " userRoles is not exist"));
//
//        List<Store> stores = new ArrayList<>();
//        for (AccountRole accountRole : accountRoles) {
//            stores.add(accountRole.getStore());
//        }
//        return stores;
//    }

    @Transactional
    public void putStore(Store store, StoreForm storeForm) {
        store.setName(storeForm.getName());
        store.setLocation(storeForm.getLocation());
        store.setPhone(storeForm.getPhone());
        store.setOpen(storeForm.getOpen());
        store.setClosed(storeForm.getClosed());
        store.setIdx(-1);
        store.setPayday(storeForm.getPayday());
        store.setStartDayOfWeek(storeForm.getStartDayOfWeek());
        store.setDeadlineOfSubmit(storeForm.getDeadlineOfSubmit());
    }

    @Transactional
    public void inStore(Store store, AccountRole role) {
        role.setStore(store);
    }
}
