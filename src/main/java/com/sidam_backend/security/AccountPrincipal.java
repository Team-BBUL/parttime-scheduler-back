package com.sidam_backend.security;

import com.sidam_backend.data.Account;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AccountPrincipal implements OAuth2User, UserDetails {

    private Long id;
    private String name;
    private String email;
    private String oauth2Id;
    private Collection<? extends GrantedAuthority> authorities;

    @Setter
    private Map<String, Object> attributes;

    public AccountPrincipal(Long id, String name, String email, String oauth2Id, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.oauth2Id = oauth2Id;
        this.authorities = authorities;
    }

    public static AccountPrincipal create(Account account, Map<String, Object> attributes){
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(account.getRoleKey()));
        AccountPrincipal accountPrincipal = new AccountPrincipal(
                account.getId(), account.getName(), account.getEmail(), account.getOauth2Id(), authorities);
        accountPrincipal.setAttributes(attributes);
        return accountPrincipal;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return String.valueOf(id);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return String.valueOf(oauth2Id);
    }
}
