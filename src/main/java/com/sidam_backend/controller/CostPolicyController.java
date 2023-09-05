package com.sidam_backend.controller;

import com.sidam_backend.data.AccountRole;
import com.sidam_backend.data.CostPolicy;
import com.sidam_backend.data.Store;
import com.sidam_backend.resources.DTO.PostPolicy;
import com.sidam_backend.service.CostPolicyService;
import com.sidam_backend.service.EmployeeService;
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
public class CostPolicyController {

    private final CostPolicyService costPolicyService;

    private final EmployeeService employeeService;

    @GetMapping(value = "/stores/{storeId}/costpolicies", produces = "application/json; charset=UTF-8" )
    public ResponseEntity<Map<String,Object>> allPolicy(
            @AuthenticationPrincipal Long id,
            @PathVariable Long storeId
    ){
        Map<String, Object> res = new HashMap<>();
        log.info("allPolicy progress");
        try {
            Store store = employeeService.validateStoreId(storeId);
            AccountRole accountRole = employeeService.getEmployeeByAccountId(store, id);
            if(!accountRole.isManager()){
                throw new AccessDeniedException("No Authority");
            }
            log.info("get all employee: Store" + storeId);

            List<CostPolicy> result = costPolicyService.getAllPolicy(store);

            res.put("data", result);
            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException ex) {
            res.put("status_code", 400);
            res.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(res);
        }
    }

    @PostMapping(value = "/stores/{storeId}/costpolicy" )
    public ResponseEntity<Map<String,Object>> postPolicy(
            @AuthenticationPrincipal Long id,
            @PathVariable Long storeId,
            @RequestBody PostPolicy postPolicy
            ){
        Map<String, Object> res = new HashMap<>();
        try {
            Store store = employeeService.validateStoreId(storeId);
            AccountRole accountRole = employeeService.getEmployeeByAccountId(store, id);
            if(!accountRole.isManager()){
                throw new AccessDeniedException("No Authority");
            }
            log.info("get all employee: Store" + storeId);

            CostPolicy newPolicy = costPolicyService.createNewPolicy(postPolicy, store);

            res.put("id", newPolicy.getId());
            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException ex) {
            res.put("status_code", 400);
            res.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(res);
        }
    }

    @DeleteMapping("stores/{storeId}/costpolicies/{policyId}")
    public ResponseEntity<Map<String, Object>> removePolicy(
            @AuthenticationPrincipal Long id,
            @PathVariable Long storeId,
            @PathVariable Long policyId
    ){
        Map<String, Object> res = new HashMap<>();
        try {
            Store store = employeeService.validateStoreId(storeId);
            AccountRole accountRole = employeeService.getEmployeeByAccountId(store, id);
            if(!accountRole.isManager()){
                throw new AccessDeniedException("No Authority");
            }
            log.info("get all employee: Store" + storeId);

            costPolicyService.deletePolicy(policyId);

            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException ex) {
            res.put("status_code", 400);
            res.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(res);
        }
    }

}
