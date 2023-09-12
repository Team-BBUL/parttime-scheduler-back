package com.sidam_backend.security;

import com.sidam_backend.data.AccountRole;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

@Getter
public class UserAccount extends User {

    private AccountRole accountRole;

    public UserAccount(AccountRole accountRole) {
        super(accountRole.getAccountId(), accountRole.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
        this.accountRole = accountRole;
    }
}
