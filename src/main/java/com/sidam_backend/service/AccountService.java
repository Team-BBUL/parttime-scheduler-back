package com.sidam_backend.service;

import com.sidam_backend.data.Account;
import com.sidam_backend.repo.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public void saveAccount(Long id, Map<String, Object> resultMap){
        Account account =  this.getAccount(id);
        account.setName((String) resultMap.get("name"));
        //account.setOnceVerified(true);
        accountRepository.save(account);
    }

    public Account getAccount(Long id){
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(id + "에 해당하는 계정이 없습니다"));
        return account;
    }
}
