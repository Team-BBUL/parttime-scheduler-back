package com.sidam_backend.service;

import com.sidam_backend.data.AccountRole;
import com.sidam_backend.repo.AccountRoleRepository;
import com.sidam_backend.resources.DTO.LoginForm;
import com.sidam_backend.resources.DTO.SignUpForm;
import com.sidam_backend.resources.DTO.UpdateAccount;
import com.sidam_backend.security.AccountDetail;
import com.sidam_backend.security.TokenProvider;
import com.sidam_backend.security.UserAccount;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final AccountRoleRepository accountRoleRepository;
    private final TokenProvider tokenProvider;

    public AccountRole processNewAccount(SignUpForm signUpForm) {
        AccountRole accountRole = new AccountRole();
        accountRole.setAccountId(signUpForm.getAccountId());
        accountRole.setPassword(passwordEncoder.encode(signUpForm.getPassword()));

        accountRole.setOriginAccountId(signUpForm.getAccountId());
        accountRole.setOriginPassword(passwordEncoder.encode(signUpForm.getPassword()));

        accountRole.setRole(signUpForm.getRole());
        return accountRoleRepository.save(accountRole);
    }

    @Transactional
    public void completeSignup(UpdateAccount updateAccount, Long id) {
        AccountRole accountRole = this.getAccount(id);
        accountRole.setAlias(updateAccount.getAlias());
        accountRole.setLevel(updateAccount.getLevel());
        accountRole.setSalary(updateAccount.isSalary());
        accountRole.setColor(updateAccount.getColor());
        accountRole.completeSignUp();
        accountRole.setOriginAccountId(accountRole.getAccountId());
        accountRole.setOriginPassword(accountRole.getPassword());
        doLogin(accountRole);
//        return accountRoleRepository.save(accountRole);
    }

    public AccountRole getAccount(Long id){
        return accountRoleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(id + " AccountRole is not exist."));
    }



    public String doLogin(AccountRole accountRole) {
        AccountDetail accountDetail = new AccountDetail();
        accountDetail.setId(accountRole.getId());
        accountDetail.setAccountId(accountRole.getAccountId());
        accountDetail.setRole(accountRole.getRole());
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        accountDetail,
                        accountRole.getPassword(),
                        List.of(new SimpleGrantedAuthority(accountRole.getRole().name())));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = tokenProvider.create(accountDetail);
        return token;
    }

    @Override
    public UserDetails loadUserByUsername(String accountId) throws UsernameNotFoundException {
        AccountRole accountRole = accountRoleRepository.findByAccountId(accountId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 아이디가 없습니다"));
        return new UserAccount(accountRole);
    }

    public AccountRole preprocessLogin(LoginForm loginRequest) {
        AccountRole accountRole = accountRoleRepository.findByAccountId(loginRequest.getAccountId())
                .orElseThrow(() -> new IllegalArgumentException("해당하는 아이디가 없습니다"));
        if(!passwordEncoder.matches(loginRequest.getPassword(), accountRole.getPassword())){
            throw new IllegalArgumentException("아이디와 비밀번호를 확인해주세요");
        }
        return accountRole;
    }
}
