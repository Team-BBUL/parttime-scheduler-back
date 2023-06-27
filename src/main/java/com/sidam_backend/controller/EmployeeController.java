package com.sidam_backend.controller;


import com.sidam_backend.data.UserRole;
import com.sidam_backend.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/employees/{storeId}")
    public ResponseEntity<Map<String, Object>> allEmployee(@PathVariable Long storeId) {

        Map<String, Object> response = new HashMap<>();

        log.info("get all employee: Store" + storeId);
        List<UserRole> res = employeeService.getAllEmployees(storeId);

        response.put("data", res);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employee/{storeId}")
    public ResponseEntity<UserRole> singleEmployee(
            @PathVariable Long storeId,
            @RequestParam("id") Long roleId) {

        log.info("get a employee: store " + storeId + " UserRole " + roleId);

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

        log.info("register a employee: Store " + storeId + "/ User " + userId);

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
            @RequestParam(value = "id") Long userId,
            @Valid UserRole editUser) {

        log.info("edit employee: Store " + storeId + "/ UserRole " + userId);

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
        log.info("delete employee: Store " + storeId + "/ UserRole " + userId);

        try {
            employeeService.deleteEmployee(storeId, userId);
            return ResponseEntity.ok().body("delete successful");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
