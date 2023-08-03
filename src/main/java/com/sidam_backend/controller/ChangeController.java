package com.sidam_backend.controller;

import com.sidam_backend.data.ChangeRequest;
import com.sidam_backend.data.Store;
import com.sidam_backend.service.ChangeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/schedule/change")
@RequiredArgsConstructor
public class ChangeController {

    private final ChangeService changeService;

    // 대상 비지정 근무 교환 요청
    @PostMapping("/{storeId}")
    public ResponseEntity<Map<String, Object>> postRequest(
            @PathVariable("storeId") Long storeId,
            @RequestParam("id") Long roleId,
            @RequestParam("schedule") Long scheduleId
    ) {
        Map<String, Object> response = new HashMap<>();
        Store store;
        ChangeRequest request;

        log.info("unassigned work change request : user_role" + roleId + " schedule" + scheduleId);

        try {
            store = changeService.validateStoreId(storeId);
            changeService.validateRoleId(roleId);
            changeService.validateSchedule(scheduleId);

            request = changeService.saveChangeRequest(roleId, scheduleId);

        } catch (Exception ex) {
            response.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(response);
        }

        // 점주에 대해 알람 저장
        changeService.changeAlarmMaker(store, request);

        response.put("message", "save success");

        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/assignment/{storeId}")
    public ResponseEntity<Map<String, Object>> postAssignedRequest(
            @PathVariable("storeId") Long storeId,
            @RequestParam("id") Long roleId,
            @RequestParam("schedule") Long scheduleId,
            @RequestParam("target") Long targetId,
            @RequestParam("objective") Long objectiveId
    ) {
        Map<String, Object> response = new HashMap<>();
        Store store;
        ChangeRequest request;

        log.info("assigned work change request : " + roleId + " to " + targetId +
                " schedule " + scheduleId + " to " + objectiveId);

        try {
            store = changeService.validateStoreId(storeId);
            changeService.validateRoleId(roleId);
            changeService.validateRoleId(targetId);
            changeService.validateSchedule(scheduleId);
            changeService.validateSchedule(objectiveId);

            request = changeService.saveChangeRequest(roleId, targetId, scheduleId, objectiveId);

        } catch (Exception ex) {
            response.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(response);
        }

        // 점주와 요청 대상자에 대해 알람 저장
        changeService.changeAlarmMaker(store, request);

        response.put("message", "save success");

        return ResponseEntity.ok().body(response);
    }
}
