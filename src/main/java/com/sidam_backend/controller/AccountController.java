package com.sidam_backend.controller;

import com.sidam_backend.data.Account;
import com.sidam_backend.resources.AccountForm;
import com.sidam_backend.service.AccountService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class AccountController {

    private final AccountService accountService;

    @GetMapping(produces="application/json; charset=UTF-8")
    public ResponseEntity<Map<String,Object>> getAccountInfo(
            @AuthenticationPrincipal Long id
    ){
        Map<String, Object> res = new HashMap<>();
        AccountForm accountForm = new AccountForm();

        try{

            log.info("email = {}", id);
            Account account = accountService.getAccount(id);
            accountForm.setName(account.getName());
            res.put("data",accountForm);

            return ResponseEntity.ok().body(res);
        }catch (Exception e){
            log.info(e.getMessage());

            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/regist")
    public ResponseEntity<Map<String, Object>> register(
            @AuthenticationPrincipal Long id,
            @RequestBody Map<String, Object> resultMap
    ){
        Map<String, Object> response = new HashMap<>();

        try{
            log.info("id = {}", id);
            log.info("resultMap = {}", resultMap);
            accountService.saveAccount(id, resultMap);
            response.put("status_code", 200);
            response.put("data", "성공했습니다");

            return ResponseEntity.ok().body(response);
        }catch (Exception e){
            log.info(e.getMessage());
            response.put("status_code", 400);
            response.put("data", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }
}
