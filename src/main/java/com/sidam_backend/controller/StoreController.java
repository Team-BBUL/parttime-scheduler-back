package com.sidam_backend.controller;


import com.sidam_backend.data.AccountRole;
import com.sidam_backend.data.Store;
import com.sidam_backend.data.enums.Role;
import com.sidam_backend.resources.DTO.StoreForm;
import com.sidam_backend.security.AccountDetail;
import com.sidam_backend.service.EmployeeService;
import com.sidam_backend.service.StoreService;
import com.sidam_backend.validator.StoreValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
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

    @GetMapping(value = "/search", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Map<String, Object>> searchStore(
            @RequestParam("input") String input
    ) {

        Map<String, Object> res = new HashMap<>();

        log.info("search store: input = " + input + ", " + input.length() + "L");

        if (input.length() < 2) {
            res.put("message", "input is too short.");
            return ResponseEntity.badRequest().body(res);
        }

        try {
            res.put("data", storeService.findStore(input));
            return ResponseEntity.ok(res);

        } catch (IllegalArgumentException ex) {
            res.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(res);
        }
    }

    @GetMapping(value = "/search/all", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Map<String, Object>> searchAllStore() {

        Map<String, Object> res = new HashMap<>();

        log.info("search all store name");

        res.put("data", storeService.findAllStoreName());
        return ResponseEntity.ok(res);
    }

    @GetMapping(value = "/my-list", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Map<String, Object>> getMyStores(
            @AuthenticationPrincipal AccountDetail accountDetail,

            @RequestParam Role role
    ) {
        Map<String, Object> res = new HashMap<>();

        try {
            log.info("search my stores");
//            List<Store> stores = storeService.getMyStores(id, role);
//            res.put("data", stores);
            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException ex) {
            res.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(res);
        }
    }



    @PostMapping(value = "/regist", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Map<String, Object>> register(
            @AuthenticationPrincipal AccountDetail accountDetail,

            @RequestBody @Valid StoreForm storeForm,
            Errors errors
    ) {
        Map<String, Object> response = new HashMap<>();

        log.info("storeForm = {}", storeForm);

        if (errors.hasErrors()) {
            response.put("data", errors.getAllErrors());
            return ResponseEntity.badRequest().body(response);
        }

        try {
            AccountRole accountRole = storeService.findById(accountDetail.getId());
            Store newStore = storeService.createNewStore(storeForm);

            storeService.inStore(newStore, accountRole);

            response.put("status_code", 200);
            response.put("store_id", newStore.getId());

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {

            response.put("status_code", 400);
            response.put("data", e.getMessage());
            log.info("StoreController.ResponseEntity.badRequest().body(response) ={}",
                    ResponseEntity.badRequest().body(response));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping(value = "/add/{storeId}", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Map<String, Object>> addStore(
            @AuthenticationPrincipal AccountDetail accountDetail,

            @PathVariable Long storeId
    ) {
        Map<String, Object> response = new HashMap<>();

        try {
            Store store = storeService.findStoreById(storeId);

            response.put("store_id", store.getId());
            response.put("status_code", 200);
            response.put("data", "성공했습니다");

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {

            response.put("status_code", 400);
            response.put("data", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/withdraw/{storeId}")
    public ResponseEntity<Map<String, Object>> withdrawStore(
            @AuthenticationPrincipal AccountDetail accountDetail,

            @PathVariable Long storeId,
            @RequestParam("id") Long roleId
    ) {
        Map<String, Object> response = new HashMap<>();

        try {
            Store store = employeeService.validateStoreId(storeId);
            AccountRole accountRole = employeeService.getMyInfo(store, roleId);
//            accountRole.isValidEmail(email);
            employeeService.deleteEmployee(store, roleId);

            response.put("status_code", 200);
            response.put("data", "성공했습니다");

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {

            response.put("status_code", 400);
            response.put("data", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping(value = "/{storeId}", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Map<String, Object>> getStore(
            @AuthenticationPrincipal AccountDetail accountDetail,

            @PathVariable Long storeId
    ) {
        Map<String, Object> response = new HashMap<>();

        try {
            Store store = employeeService.validateStoreId(storeId);
            AccountRole owner = employeeService.getMyInfo(accountDetail.getAccountId());
            if(!storeId.equals(owner.getStore().getId()) || !owner.isManager()){
                throw new AccessDeniedException("No Authority");
            }
            response.put("status_code", 200);
            response.put("data", store);

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {

            response.put("status_code", 400);
            response.put("data", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping(value = "/{storeId}", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Map<String, Object>> modifyStore(
            @AuthenticationPrincipal AccountDetail accountDetail,
            @PathVariable Long storeId,
            @RequestBody StoreForm storeForm
    ) {
        Map<String, Object> response = new HashMap<>();

        log.info("modifyStore = {}", storeId);
        try {
            Store store = employeeService.validateStoreId(storeId);
            AccountRole owner = employeeService.getMyInfo(accountDetail.getAccountId());
            if(!storeId.equals(owner.getStore().getId()) || !owner.isManager()){
                throw new AccessDeniedException("No Authority");
            }
            storeService.putStore(store, storeForm);
            response.put("status_code", 200);
            response.put("data", store);

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {

            response.put("status_code", 400);
            response.put("data", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }
}
