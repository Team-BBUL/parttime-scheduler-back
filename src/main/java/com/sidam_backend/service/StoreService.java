package com.sidam_backend.service;

import com.sidam_backend.data.Account;
import com.sidam_backend.data.AccountRole;
import com.sidam_backend.data.Store;
import com.sidam_backend.data.enums.Role;
import com.sidam_backend.repo.AccountRepository;
import com.sidam_backend.repo.AccountRoleRepository;
import com.sidam_backend.repo.StoreRepository;
import com.sidam_backend.resources.StoreForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final AccountRoleRepository accountRoleRepository;
    private final AccountRepository accountRepository;

    public Store createNewStore(StoreForm storeForm) {
        Store store = new Store();
        store.setName(storeForm.getName());
        store.setLocation(storeForm.getLocation());
        store.setPhone(storeForm.getPhone());
        store.setOpen(storeForm.getOpen());
        store.setClosed(storeForm.getClosed());
        store.setIdx(-1);
        store.setPayday(storeForm.getPayday());
        store.setStartDayOfWeek(storeForm.getWeekStartDay());

        Store newStore = storeRepository.save(store);
        log.info("newStore = {}", newStore);

        return newStore;
    }

    public AccountRole createNewAccountRole(Store newStore, Long id, Role role) {
        Account account = accountRepository.findById(id)
                .orElseThrow(IllegalArgumentException::new);

        AccountRole newAccountRole = new AccountRole();
        newAccountRole.setAlias(account.getName());
        newAccountRole.setLevel(0);
        newAccountRole.setCost(1);
        newAccountRole.setColor("white");
        newAccountRole.setSalary(true);
        newAccountRole.setValid(true);
        newAccountRole.setStore(newStore);
        newAccountRole.setAccount(account);
        newAccountRole.setRole(role);

        AccountRole accountRole = accountRoleRepository.save(newAccountRole);

        log.info("newAccount = {}", accountRole);

        return accountRole;
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

    public List<Store> getMyStores(Long id, Role role) {
        List<AccountRole> accountRoles = accountRoleRepository.
                findAccountRolesByAccountIdAndRole(id, role)
                .orElseThrow(() -> new IllegalArgumentException(id + " userRoles is not exist"));

        List<Store> stores = new ArrayList<>();
        for (AccountRole accountRole : accountRoles) {
            stores.add(accountRole.getStore());
        }
        return stores;
    }

}
