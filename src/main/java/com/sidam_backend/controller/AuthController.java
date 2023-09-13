package com.sidam_backend.controller;

import com.sidam_backend.data.AccountRole;
import com.sidam_backend.repo.AccountRoleRepository;
import com.sidam_backend.resources.DTO.LoginForm;
import com.sidam_backend.resources.DTO.SignUpForm;
import com.sidam_backend.resources.DTO.UpdateAccount;
import com.sidam_backend.security.AccountDetail;
import com.sidam_backend.service.AuthService;
import com.sidam_backend.validator.AccountValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AccountValidator accountValidator;

    @InitBinder("SignUpForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.setValidator(accountValidator);
    }

    @PostMapping(value = "/signup")
    public ResponseEntity<Map<String, Object>> signup(
            @Valid @RequestBody SignUpForm signUpForm,
            Errors errors
    ) {
        Map<String, Object> res = new HashMap<>();

        if (errors.hasErrors()) {
            res.put("data", errors.getAllErrors());
            return ResponseEntity.badRequest().body(res);
        }

        try {
            AccountRole accountRole = authService.processNewAccount(signUpForm);
            authService.doLogin(accountRole);
            return ResponseEntity.ok().build();
        }catch (IllegalArgumentException ex){
            res.put("status_code", 400);
            res.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(res);
        }
    }

    @PutMapping(value = "/account/details")
    public ResponseEntity<Map<String, Object>> fillDetails(
            @AuthenticationPrincipal AccountDetail accountDetail,
            @RequestBody UpdateAccount updateAccount
    ){
        Map<String, Object> res = new HashMap<>();

        try{
            authService.completeSignup(updateAccount, accountDetail.getId());
            return ResponseEntity.ok().build();
        }catch (IllegalArgumentException ex){
            res.put("status_code", 400);
            res.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(res);
        }
    }

    @PostMapping(value = "/login", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Map<String,Object>> login(
            @RequestBody LoginForm loginRequest
    )
    {
        Map<String, Object> res = new HashMap<>();
        log.info("login process...");
        try{
            AccountRole accountRole = authService.preprocessLogin(loginRequest);
            String token = authService.doLogin(accountRole);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Authorization", "Bearer " + token);
            return ResponseEntity.ok().headers(responseHeaders).build();
        }catch(IllegalArgumentException ex){
            res.put("status_code", 400);
            res.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(res);
        }
    }

}
