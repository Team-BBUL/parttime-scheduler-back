package com.sidam_backend.controller;

import com.sidam_backend.data.AccountRole;
import com.sidam_backend.data.Incentive;
import com.sidam_backend.data.Store;
import com.sidam_backend.resources.DTO.GetIncentivesRoleInfo;
import com.sidam_backend.resources.DTO.PostIncentive;
import com.sidam_backend.security.AccountDetail;
import com.sidam_backend.service.EmployeeService;
import com.sidam_backend.service.IncentiveService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class IncentiveController {

    private final EmployeeService employeeService;

    private final IncentiveService incentiveService;

    @GetMapping(value = "/stores/{storeId}/incentives", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Map<String,Object>> getAllEmployeeMonthIncentive(
            @AuthenticationPrincipal AccountDetail accountDetail,
            @PathVariable Long storeId,
            @RequestParam String month
    ){
        Map<String, Object> res = new HashMap<>();

        log.info("get month incentive = {}",month);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
        YearMonth yearMonth = YearMonth.parse(month, formatter);
        LocalDate dateTime = LocalDate.from(yearMonth.atDay(1).atTime(0, 0));
        try{
            Store store = employeeService.validateStoreId(storeId);
            AccountRole owner = employeeService.getMyInfo(accountDetail.getAccountId());
            if(!storeId.equals(owner.getStore().getId()) || !owner.isManager()){
                throw new AccessDeniedException("No Authority");
            }

            List<GetIncentivesRoleInfo> data = incentiveService.
                    getWithRoleByDate(store, dateTime);

            res.put("data", data);

            return ResponseEntity.ok(res);
        }catch(IllegalArgumentException ex){

            res.put("message", ex.getMessage());
            res.put("status_code", 400);

            return ResponseEntity.badRequest().body(res);
        }
    }

    @GetMapping(value = "/stores/{storeId}/employees/{employeeId}/incentives", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Map<String,Object>> getOnesMonthIncentive(
            @AuthenticationPrincipal AccountDetail accountDetail,

            @PathVariable Long storeId,
            @PathVariable Long employeeId,
            @RequestParam String month
    ){
        Map<String, Object> res = new HashMap<>();

        log.info("get month incentive = {}",month);
        try{
            Store store = employeeService.validateStoreId(storeId);
            AccountRole owner = employeeService.getMyInfo(accountDetail.getAccountId());
            if(!employeeId.equals(accountDetail.getId())){
                if(!storeId.equals(owner.getStore().getId()) || !owner.isManager()){
                    throw new AccessDeniedException("No Authority");
                }
            }

            GetIncentivesRoleInfo data = incentiveService.getIncentivesByDate(employeeId, store.getPayday(), month);

            res.put("data", data);

            return ResponseEntity.ok(res);
        }catch(IllegalArgumentException ex){

            res.put("message", ex.getMessage());
            res.put("status_code", 400);

            return ResponseEntity.badRequest().body(res);
        }
    }

    @GetMapping(value = "/stores/{storeId}/employees/{employeeId}/incentives/all", produces="application/json; charset=UTF-8")
    public ResponseEntity<Map<String, Object>> getOnesAllIncentives(
            @AuthenticationPrincipal AccountDetail accountDetail,
            @PathVariable Long storeId,
            @PathVariable Long employeeId
    ) {

        Map<String, Object> res = new HashMap<>();

        try {
            Store store = employeeService.validateStoreId(storeId);
            AccountRole owner =employeeService.getMyInfo(accountDetail.getAccountId());
            if(!storeId.equals(owner.getStore().getId()) || !owner.isManager()){
                throw new AccessDeniedException("No Authority");
            }
            log.info("get one's all incentive = {}",employeeId);

            List<Incentive> incentives = incentiveService.getIncentives(employeeId);

            res.put("data", incentives);

            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException ex) {

            res.put("message", ex.getMessage());
            res.put("status_code", 400);

            return ResponseEntity.badRequest().body(res);
        }
    }

    @GetMapping(value = "/stores/{storeId}/employees/{employeeId}/incentives/{incentiveId}", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Map<String, Object>> getOneIncentive(
            @AuthenticationPrincipal AccountDetail accountDetail,
            @PathVariable Long storeId,
            @PathVariable Long employeeId,
            @PathVariable Long incentiveId
    ){

        Map<String, Object> res = new HashMap<>();

        try {
            Store store = employeeService.validateStoreId(storeId);
            AccountRole owner =employeeService.getMyInfo(accountDetail.getAccountId());
            if(!storeId.equals(owner.getStore().getId()) || !owner.isManager()){
                throw new AccessDeniedException("No Authority");
            }

            Incentive incentive = incentiveService.getIncentive(incentiveId);

            res.put("data", incentive);

            return ResponseEntity.ok(res);
        }catch (IllegalArgumentException ex){

            res.put("message", ex.getMessage());
            res.put("status_code", 400);

            return ResponseEntity.badRequest().body(res);
        }
    }

    @PostMapping("/stores/{storeId}/employees/{employeeId}/incentive")
    public ResponseEntity<Map<String, Object>> postIncentive(
            @AuthenticationPrincipal AccountDetail accountDetail,
            @PathVariable Long storeId,
            @PathVariable Long employeeId,
            @RequestBody PostIncentive postIncentive
            ){

        Map<String, Object> res = new HashMap<>();

        try{
            Store store = employeeService.validateStoreId(storeId);
            AccountRole owner =employeeService.getMyInfo(accountDetail.getAccountId());
            if(!storeId.equals(owner.getStore().getId()) || !owner.isManager()){
                throw new AccessDeniedException("No Authority");
            }

            Incentive newIncentive = incentiveService.createNewIncentive(postIncentive, employeeId);

            res.put("id", newIncentive.getId());
            return ResponseEntity.ok(res);
        }catch (IllegalArgumentException ex){
            res.put("message", ex.getMessage());
            res.put("status_code", 400);

            return ResponseEntity.badRequest().body(res);
        }
    }


    @PutMapping("/stores/{storeId}/employees/{employeeId}/incentives/{incentiveId}")
    public ResponseEntity<Map<String, Object>> putIncentive(
            @AuthenticationPrincipal AccountDetail accountDetail,
            @PathVariable Long storeId,
            @PathVariable Long employeeId,
            @PathVariable Long incentiveId,
            @RequestBody PostIncentive postIncentive
    ){

        Map<String, Object> res = new HashMap<>();

        try{
            Store store = employeeService.validateStoreId(storeId);
            AccountRole owner =employeeService.getMyInfo(accountDetail.getAccountId());
            if(!storeId.equals(owner.getStore().getId()) || !owner.isManager()){
                throw new AccessDeniedException("No Authority");
            }

            incentiveService.updateIncentive(postIncentive, incentiveId);

            return ResponseEntity.ok(res);
        }catch (IllegalArgumentException ex){
            res.put("message", ex.getMessage());
            res.put("status_code", 400);

            return ResponseEntity.badRequest().body(res);
        }
    }

    @DeleteMapping("/stores/{storeId}/employees/{employeeId}/incentives/{incentiveId}")
    public ResponseEntity<Map<String, Object>> removeIncentive(
            @AuthenticationPrincipal AccountDetail accountDetail,
            @PathVariable Long storeId,
            @PathVariable Long employeeId,
            @PathVariable Long incentiveId
            ) {

        Map<String, Object> res = new HashMap<>();

        log.info("removeIncentiveController = {}", incentiveId);
        try {
            Store store = employeeService.validateStoreId(storeId);
            AccountRole owner =employeeService.getMyInfo(accountDetail.getAccountId());
            if(!storeId.equals(owner.getStore().getId()) || !owner.isManager()){
                throw new AccessDeniedException("No Authority");
            }

            incentiveService.deleteIncentive(incentiveId);

            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException ex) {
            res.put("message", ex.getMessage());
            res.put("status_code", 400);

            return ResponseEntity.badRequest().body(res);
        }
    }

}
