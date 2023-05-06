package com.sidam_backend.controller;


import com.sidam_backend.data.UserRole;
import com.sidam_backend.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping("/employee/{storeId}")
    public List<UserRole> allEmployee(@PathVariable Long storeId) {
        log.info("get employees");
        return employeeService.getAllEmployees(storeId);
    }

    @PostMapping("/employee/{storeId}")
    public String registerEmployee(@PathVariable String storeId, @Valid UserRole user, Errors errors, SessionStatus sessionStatus) {
        log.info("register employee");
        return "redirect:/";
    }

    @PutMapping("/employee/{storeId}")
    public String modifyEmployee(
            @PathVariable String storeId, @RequestParam(value = "id", required = false) String userId) {
        log.info("modify employee");
        return "redirect:/";
    }

    @DeleteMapping("/employee/{storeId}")
    public String deleteEmployee(@PathVariable String storeId, @RequestParam String userId) {
        log.info("delete employee");
        return "redirect:/";
    }
}
