package com.sidam_backend.controller;


import com.sidam_backend.data.User;
import com.sidam_backend.data.UserRole;
import com.sidam_backend.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.util.List;
import java.util.ResourceBundle;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @ExceptionHandler
    public ResponseEntity<String> handleIllegalArgumentException (
            IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body("잘못된 요청입니다.");
    }

    @GetMapping("/employee/{storeId}")
    public ResponseEntity<List<UserRole>> allEmployee(@PathVariable Long storeId) {

        log.info("근무자 전체 조회: Store" + storeId);
        List<UserRole> res = employeeService.getAllEmployees(storeId);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/employee/{storeId}")
    public ResponseEntity<UserRole> singleEmployee(
            @PathVariable Long storeId,
            @RequestParam("id") Long roleId) {

        log.info("특정 근무자 조회: Store " + storeId + "의 UserRole " + roleId + " 조회");

        try {
            UserRole userRole = employeeService.getEmployee(storeId, roleId);
            return ResponseEntity.ok(userRole);
        } catch (IllegalArgumentException ex) {
            log.warn(ex.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/employee/{storeId}")
    public ResponseEntity<String> registerEmployee(
            @PathVariable Long storeId,
            @RequestParam("kakaoId") String userId) {

        log.info("근무자 매장 등록 과정: Store " + storeId + "에 User " + userId + "를 등록");

        try {
            UserRole newUser = employeeService.postEmployee(storeId, userId);
            return ResponseEntity.ok(newUser.getId().toString());
        } catch (IllegalArgumentException ex) {
            log.warn(ex.getMessage());
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PutMapping("/employee/{storeId}")
    public ResponseEntity<UserRole> modifyEmployee(
            @PathVariable Long storeId,
            @RequestParam(value = "id", required = false) Long userId,
            @Valid UserRole editUser) {

        log.info("근무자 정보 수정: Store " + storeId + "의 UserRole " + userId + "의 정보 수정");

        try {
            UserRole edit = employeeService.putEmployee(storeId, userId, editUser);
            return ResponseEntity.ok(edit);
        } catch (IllegalArgumentException ex) {
            log.warn(ex.getMessage());
            return ResponseEntity.badRequest().body(editUser);
        }
    }

    @DeleteMapping("/employee/{storeId}")
    public ResponseEntity<String> deleteEmployee(@PathVariable Long storeId, @RequestParam(value = "id") Long userId) {
        log.info("근무자 삭제: Store " + storeId + "의 UserRole " + userId + " 삭제");

        try {
            employeeService.deleteEmployee(storeId, userId);
            return ResponseEntity.ok().body("삭제 성공");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
