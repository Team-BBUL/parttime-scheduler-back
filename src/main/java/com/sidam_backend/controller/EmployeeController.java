package com.sidam_backend.controller;


import com.sidam_backend.data.Store;
import com.sidam_backend.data.AccountRole;
import com.sidam_backend.resources.DTO.LoginForm;
import com.sidam_backend.resources.DTO.PostEmployee;
import com.sidam_backend.resources.UpdateAuth;
import com.sidam_backend.security.AccountDetail;
import com.sidam_backend.service.AuthService;
import com.sidam_backend.service.EmployeeService;
import io.jsonwebtoken.Jwt;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping(value = "/account", produces="application/json; charset=UTF-8")
    public ResponseEntity<Map<String,  Object>> getMyInfo(
            @AuthenticationPrincipal AccountDetail accountDetail
    ){
        Map<String, Object> res = new HashMap<>();

        log.info("getMyInfo.AuthenticationPrincipal.accountId = {}", accountDetail.getAccountId());
        try{
            AccountRole accountRole = employeeService.getMyInfo(accountDetail.getAccountId());
            res.put("data",accountRole);
            return ResponseEntity.ok().body(res);
        }catch(IllegalArgumentException ex){

            return ResponseEntity.badRequest().build();
        }
    }
    // 근무자 가입
    @PostMapping("/store/{storeId}/register/employee")
    public ResponseEntity<Map<String, Object>> registerEmployee(
            @AuthenticationPrincipal AccountDetail accountDetail,
            @PathVariable Long storeId,
            @RequestBody PostEmployee postEmployee
    ) {
        AccountRole owner =employeeService.getMyInfo(accountDetail.getAccountId());
        if(!storeId.equals(owner.getStore().getId()) || !owner.isManager()){
            throw new AccessDeniedException("No Authority");
        }
        Map<String, Object> res = new HashMap<>();
        log.info("register a employee: Store " + storeId + "/ User " + accountDetail.getId());

        try {
//            Store store = employeeService.validateStoreId(storeId);
            AccountRole newEmployee = employeeService.processNewEmployee(owner, postEmployee);
//            AccountRole newUser = employeeService.postEmployee(store, userId);

            res.put("status_code", 200);
            res.put("message", "employee register successful");
            return ResponseEntity.ok(res);

        } catch (IllegalArgumentException ex) {

            res.put("status_code", 400);
            res.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(res);
        }
    }

    @PutMapping("/account")
    public ResponseEntity<Map<String, Object>> confirmEmployee(
            @AuthenticationPrincipal AccountDetail accountDetail,
            @RequestBody UpdateAuth updateAuth
            ) {
        Map<String, Object> res = new HashMap<>();

        try {
            AccountRole myInfo = employeeService.getMyInfo(accountDetail.getAccountId());
            if(!myInfo.isValid()){
                throw new IllegalArgumentException("정보 변경은 한 번만 가능합니다.");
            }
            if(!updateAuth.getPassword().equals(updateAuth.getCheckPassword())){
                throw new IllegalArgumentException("비밀번호가 다릅니다");
            }
            AccountRole updateEmployee = employeeService.updateAccount(updateAuth, myInfo);

            res.put("status_code", 200);
            return ResponseEntity.ok(res);

        } catch (IllegalArgumentException ex) {

            res.put("status_code", 400);
            res.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(res);
        }
    }

    @GetMapping(value = "/employees/{employeeId}/clear", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Map<String, Object>> clearEmployeeAuth(
            @AuthenticationPrincipal AccountDetail accountDetail,
            @PathVariable Long employeeId
    ) {
        Map<String, Object> res = new HashMap<>();

        try {
            AccountRole owner =employeeService.getMyInfo(accountDetail.getAccountId());

            AccountRole employee = employeeService.validateRoleId(employeeId);

            if(!employee.getStore().getId().equals(owner.getStore().getId()) || !owner.isManager()){
                throw new AccessDeniedException("No Authority");
            }
            employeeService.clearAuth(employee);
            res.put("status_code", 200);
            res.put("message", "employee clearAuth successful");
            return ResponseEntity.ok(res);
        }catch (IllegalArgumentException ex){
            res.put("status_code", 400);
            res.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(res);
        }
    }
    // 모든 근무자 조회
    @GetMapping(value = "/employees/{storeId}", produces="application/json; charset=UTF-8")
    public ResponseEntity<Map<String, Object>> allEmployee(
            @AuthenticationPrincipal AccountDetail accountDetail,
            @PathVariable Long storeId
    ) {
        Map<String, Object> res = new HashMap<>();

        try {
            Store store = employeeService.validateStoreId(storeId);
            AccountRole owner = employeeService.getMyInfo(accountDetail.getAccountId());
            if(!storeId.equals(owner.getStore().getId()) || !owner.isManager()){
                throw new AccessDeniedException("No Authority");
            }
//            AccountRole accountRole = employeeService.getEmployeeByAccountId(store, id);

            log.info("get all employee: Store" + storeId);

            List<AccountRole> result = employeeService.getAllEmployees(store);

            res.put("data", result);
            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException ex) {
            res.put("status_code", 400);
            res.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(res);
        }
    }

    // 단일 유저 조회
    @GetMapping(value = "/employee/{storeId}", produces="application/json; charset=UTF-8")
    public ResponseEntity<AccountRole> singleEmployee(
            @PathVariable Long storeId,
            @RequestParam("id") Long employeeId
    ) {

        log.info("get a employee: store " + storeId + " UserRole " + employeeId);

        try {
            Store store = employeeService.validateStoreId(storeId);
            AccountRole accountRole = employeeService.getMyInfo(store, employeeId);
            return ResponseEntity.ok(accountRole);
        } catch (IllegalArgumentException ex) {
            log.warn(ex.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    // 직원 정보 변경
    @PutMapping("/employee/{storeId}")
    public ResponseEntity<AccountRole> modifyEmployee(
            @PathVariable Long storeId,
            @RequestParam(value = "id") Long userId,
            @Valid @RequestBody AccountRole editUser
    ) {

        log.info("edit employee: Store " + storeId + "/ UserRole " + userId);

        try {
            Store store = employeeService.validateStoreId(storeId);
            AccountRole edit = employeeService.putEmployee(store, userId, editUser);
            return ResponseEntity.ok(edit);
        } catch (IllegalArgumentException ex) {
            log.warn(ex.getMessage());
            return ResponseEntity.badRequest().body(editUser);
        }
    }

    // 직원 삭제
    @DeleteMapping("/employee/{storeId}")
    public ResponseEntity<String> deleteEmployee(
            @PathVariable Long storeId,
            @RequestParam(value = "id") Long userId
    ) {
        log.info("delete employee: Store " + storeId + "/ UserRole " + userId);

        try {
            Store store = employeeService.validateStoreId(storeId);
            employeeService.deleteEmployee(store, userId);
            return ResponseEntity.ok().body("delete successful");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping(value  = "/enter/{storeId}",produces="application/json; charset=UTF-8")
    public ResponseEntity<Map<String,Object>> enter(
            @AuthenticationPrincipal Long id,
            @PathVariable Long storeId
    ){
        Map<String, Object> res = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        try{

            Store store = employeeService.validateStoreId(storeId);
//            AccountRole accountRole = employeeService.getAccountRoleWithStore(store, id);
//
//            log.info("enter success = {}", accountRole);
//
//            if (!accountRole.isValid()){
//                employeeService.managerAlarmMaker(store, accountRole.getAlias(), Alarm.Category.JOIN, Alarm.State.NON, accountRole.getId());
//            }
//
//            data.put("accountRole", accountRole);
//            data.put("store", accountRole.getStore());

            res.put("data",data);
            return ResponseEntity.ok(res);
        }catch (IllegalArgumentException ex){
            log.warn(ex.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(value = "/employee/{storeId}/account", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Map<String,Object>> singleEmployeeWithAccount(
            @PathVariable Long storeId,
            @RequestParam("id") Long employeeId
    ) {
        Map<String, Object> res = new HashMap<>();
        Map<String, Object> data = new HashMap<>();

        log.info("get a employee: store " + storeId + " UserRole " + employeeId);

        try {

            Store store = employeeService.validateStoreId(storeId);
            AccountRole accountRole = employeeService.getMyInfo(store, employeeId);

            log.info("singleEmployeeWithAccount = {}", accountRole);

            data.put("accountRole", accountRole);
            res.put("data", data);

            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException ex) {
            log.warn(ex.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
