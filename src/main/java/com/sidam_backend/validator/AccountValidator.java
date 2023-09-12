package com.sidam_backend.validator;

import com.sidam_backend.repo.AccountRoleRepository;
import com.sidam_backend.resources.DTO.SignUpForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountValidator implements Validator {

    private final AccountRoleRepository accountRoleRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(SignUpForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SignUpForm signUpForm = (SignUpForm) target;
        if(accountRoleRepository.existsAccountRolesByAccountId(signUpForm.getAccountId())){
            errors.rejectValue("accountId", "account_id_duplicated", "아이디명이 중복되었습니다");
        }
    }
}
