package com.sidam_backend.controller;


import com.sidam_backend.data.Account;
import com.sidam_backend.data.AccountRole;
import com.sidam_backend.data.Store;
import com.sidam_backend.data.enums.Role;
import com.sidam_backend.resources.StoreForm;
import com.sidam_backend.service.AccountService;
import com.sidam_backend.service.EmployeeService;
import com.sidam_backend.service.StoreService;
import com.sidam_backend.validator.StoreValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/store")
public class StoreController {

    private final StoreService storeService;
    private final EmployeeService employeeService;
    private final StoreValidator storeValidator;

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.setValidator(storeValidator);
    }

    @PostMapping(value = "/regist", produces="application/json; charset=UTF-8")
    public ResponseEntity<Map<String, Object>> register(
            @AuthenticationPrincipal Long id,
            @RequestBody @Valid StoreForm storeForm,
            Errors errors
            ){
        Map<String, Object> response = new HashMap<>();

        log.info("storeForm = {}",storeForm);

        if (errors.hasErrors()) {
            response.put("data", errors.getAllErrors());
            return ResponseEntity.badRequest().body(response);
        }

        try{

            Store newStore = storeService.createNewStore(storeForm);
            AccountRole newAccountRole = storeService.
                    createNewAccountRole(newStore, id, Role.MANAGER);

            response.put("status_code", 200);
            response.put("store_id", newStore.getId());

            return ResponseEntity.ok().body(response);
        }catch(Exception e){

            response.put("status_code", 400);
            response.put("data", e.getMessage());
            log.info("StoreController.ResponseEntity.badRequest().body(response) ={}",
                    ResponseEntity.badRequest().body(response));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/add/{storeId}")
    public ResponseEntity<Map<String, Object>> addStore(
            @AuthenticationPrincipal Long id,
            @PathVariable Long storeId
    ){
        Map<String, Object> response = new HashMap<>();

        try{
            Store store = storeService.findStore(storeId);
            AccountRole newAccountRole = storeService.
                    createNewAccountRole(store, id, Role.EMPLOYEE);

            response.put("status_code", 200);
            response.put("data", "성공했습니다");

            return ResponseEntity.ok().body(response);
        }catch(Exception e){

            response.put("status_code", 400);
            response.put("data", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/withdraw/{storeId}")
    public ResponseEntity<Map<String,Object>> withdrawStore(
            @AuthenticationPrincipal String email,
            @PathVariable Long storeId,
            @RequestParam("id") Long roleId
    ) {
        Map<String, Object> response = new HashMap<>();

        try {
            AccountRole accountRole = employeeService.getEmployee(storeId, roleId);
            accountRole.isValidEmail(email);
            employeeService.deleteEmployee(storeId, roleId);

            response.put("status_code", 200);
            response.put("data", "성공했습니다");

            return ResponseEntity.ok().body(response);
        }catch (Exception e){

            response.put("status_code", 400);
            response.put("data", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }
}
