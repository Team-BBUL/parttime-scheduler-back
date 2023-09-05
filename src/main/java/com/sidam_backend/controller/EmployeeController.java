package com.sidam_backend.controller;


import com.sidam_backend.data.Alarm;
import com.sidam_backend.data.Store;
import com.sidam_backend.data.AccountRole;
import com.sidam_backend.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    // 모든 근무자 조회
    @GetMapping(value = "/employees/{storeId}", produces="application/json; charset=UTF-8")
    public ResponseEntity<Map<String, Object>> allEmployee(
            @AuthenticationPrincipal Long id,
            @PathVariable Long storeId
    ) {
        Map<String, Object> res = new HashMap<>();

        try {
            Store store = employeeService.validateStoreId(storeId);
            AccountRole accountRole = employeeService.getEmployeeByAccountId(store, id);
            if(!accountRole.isManager()){
                throw new AccessDeniedException("No Authority");
            }
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
            AccountRole accountRole = employeeService.getEmployee(store, employeeId);
            return ResponseEntity.ok(accountRole);
        } catch (IllegalArgumentException ex) {
            log.warn(ex.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }


    // 근무자 가입
    @PostMapping("/employee/{storeId}")
    public ResponseEntity<Map<String, Object>> registerEmployee(
            @PathVariable Long storeId,
            @RequestParam("kakaoId") String userId
    ) {

        Map<String, Object> res = new HashMap<>();
        log.info("register a employee: Store " + storeId + "/ User " + userId);

        try {
            Store store = employeeService.validateStoreId(storeId);
            AccountRole newUser = employeeService.postEmployee(store, userId);

            // 알림 저장
//            employeeService.managerAlarmMaker(store, newUser.getAlias().toString(),
//                    Alarm.Category.JOIN, Alarm.State.NON, newUser.getId());
            // 알림 서버에 전송?

            res.put("status_code", 200);
            res.put("message", "employee register successful");
            return ResponseEntity.ok(res);

        } catch (IllegalArgumentException ex) {

            res.put("status_code", 400);
            res.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(res);
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
            AccountRole accountRole = employeeService.getAccountRoleWithStore(store, id);

            log.info("enter success = {}", accountRole);

            if (!accountRole.isValid()){
                employeeService.managerAlarmMaker(store, accountRole.getAlias(), Alarm.Category.JOIN, Alarm.State.NON, accountRole.getId());
            }

            data.put("accountRole", accountRole);
            data.put("store", accountRole.getStore());

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
            AccountRole accountRole = employeeService.getEmployee(store, employeeId);

            log.info("singleEmployeeWithAccount = {}", accountRole);

            data.put("accountRole", accountRole);
            data.put("account", accountRole.getAccount());
            res.put("data", data);

            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException ex) {
            log.warn(ex.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
