package com.sidam_backend.controller;

import com.sidam_backend.data.AccountRole;
import com.sidam_backend.data.Anniversary;
import com.sidam_backend.data.Store;
import com.sidam_backend.resources.DTO.PostAnniversary;
import com.sidam_backend.service.AnniversaryService;
import com.sidam_backend.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AnniversaryController {

    private final EmployeeService employeeService;

    private final AnniversaryService anniversaryService;

    @GetMapping(value = "/stores/{storeId}/employees/{employeeId}/anniversary",
            produces = "application/json; charset=UTF-8")
    public ResponseEntity<Map<String, Object>> getAnniversaries(
            @AuthenticationPrincipal Long id,
            @PathVariable Long storeId,
            @PathVariable Long employeeId
    ){

        Map<String, Object> res = new HashMap<>();

        try {
            Store store = employeeService.validateStoreId(storeId);
//            AccountRole accountRole = employeeService.getEmployeeByAccountId(store, id);
//            if(!accountRole.isManager()){
//                throw new AccessDeniedException("No Authority");
//            }

            AccountRole employee = employeeService.getMyInfo(store, employeeId);
            List<Anniversary> anniversaries = anniversaryService.getAnniversaries(employee);
            res.put("data", anniversaries);
            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException ex) {
            res.put("status_code", 400);
            res.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(res);
        }
    }

    @GetMapping(value = "/stores/{storeId}/employees/{employeeId}/anniversary/{anniversaryId}",
            produces = "application/json; charset=UTF-8")
    public ResponseEntity<Map<String, Object>> getAnniversaryInfo(
            @AuthenticationPrincipal Long id,
            @PathVariable Long storeId,
            @PathVariable Long employeeId,
            @PathVariable Long anniversaryId
    ){

        Map<String, Object> res = new HashMap<>();

        try {
            Store store = employeeService.validateStoreId(storeId);
//            AccountRole accountRole = employeeService.getEmployeeByAccountId(store, id);
//            if(!accountRole.isManager()){
//                throw new AccessDeniedException("No Authority");
//            }

            Anniversary anniversary = anniversaryService.getAnniversary(anniversaryId);

            res.put("data", anniversary);
            return ResponseEntity.ok().body(res);
        } catch (IllegalArgumentException ex) {
            res.put("status_code", 400);
            res.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(res);
        }
    }

    @PostMapping("/stores/{storeId}/employees/{employeeId}/anniversary")
    public ResponseEntity<Map<String, Object>> postAnniversary(
            @AuthenticationPrincipal Long id,
            @PathVariable Long storeId,
            @PathVariable Long employeeId,
            @RequestBody PostAnniversary postAnniversary
            ){
        Map<String, Object> res = new HashMap<>();

        try {
            Store store = employeeService.validateStoreId(storeId);
//            AccountRole accountRole = employeeService.getEmployeeByAccountId(store, id);
//            if(!accountRole.isManager()){
//                throw new AccessDeniedException("No Authority");
//            }
            AccountRole employee = employeeService.getMyInfo(store, employeeId);
            Anniversary newAnniversary = anniversaryService.createNewAnniversary(postAnniversary, employee);

            res.put("id", newAnniversary.getId());
            return ResponseEntity.ok().body(res);
        } catch (IllegalArgumentException ex) {
            res.put("status_code", 400);
            res.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(res);
        }
    }

    @PutMapping("/stores/{storeId}/employees/{employeeId}/anniversary/{anniversaryId}")
    public ResponseEntity<Map<String, Object>> putAnniversary(
            @AuthenticationPrincipal Long id,
            @PathVariable Long storeId,
            @PathVariable Long employeeId,
            @PathVariable Long anniversaryId,
            @RequestBody PostAnniversary postAnniversary

    ){
        Map<String, Object> res = new HashMap<>();

        try {
            Store store = employeeService.validateStoreId(storeId);
//            AccountRole accountRole = employeeService.getEmployeeByAccountId(store, id);
//            if(!accountRole.isManager()){
//                throw new AccessDeniedException("No Authority");
//            }
            Anniversary anniversary = anniversaryService.updateAnniversary(postAnniversary, anniversaryId);

            res.put("data", anniversary);
            return ResponseEntity.ok().body(res);
        } catch (IllegalArgumentException ex) {
            res.put("status_code", 400);
            res.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(res);
        }
    }

    @DeleteMapping("/stores/{storeId}/employees/{employeeId}/anniversary/{anniversaryId}")
    public ResponseEntity<Map<String, Object>> deleteAnniversary(
            @AuthenticationPrincipal Long id,
            @PathVariable Long storeId,
            @PathVariable Long employeeId,
            @PathVariable Long anniversaryId
    ) {

        Map<String, Object> res = new HashMap<>();

        try {
            Store store = employeeService.validateStoreId(storeId);
//            AccountRole accountRole = employeeService.getEmployeeByAccountId(store, id);
//            if(!accountRole.isManager()){
//                throw new AccessDeniedException("No Authority");
//            }
            anniversaryService.deleteAnniversary(anniversaryId);

            return ResponseEntity.ok().body(res);
        } catch (IllegalArgumentException ex) {
            res.put("status_code", 400);
            res.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(res);
        }
    }
}
