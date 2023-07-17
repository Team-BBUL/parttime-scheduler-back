package com.sidam_backend.controller;

import com.sidam_backend.data.UserRole;
import com.sidam_backend.data.WorkAlarm;
import com.sidam_backend.resources.DTO.GetAlarm;
import com.sidam_backend.service.AlarmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/api/alarm")
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;

    // 근무 전 알림 조회
    @GetMapping("/work/{id}")
    public ResponseEntity<Map<String, Object>> getBeforeAlarm(
            @PathVariable("id") Long roleId
    ) {

        Map<String, Object> response = new HashMap<>();

        log.info("get before work alarms : " + roleId);

        List<Integer> alarms;
        UserRole role;
        try {
            role = alarmService.validateRole(roleId);
            alarms = alarmService.getWorkAlarm(role);
        } catch (IllegalArgumentException ex) {
            response.put("status_code", 400);
            response.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(response);
        }

        response.put("times", alarms);
        return ResponseEntity.ok(response);
    }

    // 근무 전 알림 수정
    @PostMapping("/work/{id}")
    public ResponseEntity<Map<String, Object>> postBeforeAlarm(
            @PathVariable("id") Long roleId,
            @RequestParam("term") List<Integer> term
    ) {

        Map<String, Object> response = new HashMap<>();

        log.info("set before work alarm : before " + term + "m of " + roleId);

        UserRole role;
        try {
            role = alarmService.validateRole(roleId);
            alarmService.updateWorkAlarm(term, role);
        } catch (IllegalArgumentException ex) {
            response.put("message", ex.getMessage());
            response.put("status_code", 400);
            return ResponseEntity.badRequest().body(response);
        }

        response.put("message", "save success");
        response.put("status_code", 200);
        return ResponseEntity.ok(response);
    }

    // 알림 목록 조회
    @GetMapping("/list/{roleId}")
    public ResponseEntity<Map<String, Object>> getAlarmList(
            @PathVariable Long roleId
    ) {
        Map<String, Object> res = new HashMap<>();

        log.info("get alarm list: id " + roleId);

        UserRole role;
        List<GetAlarm> result;
        try {
            role = alarmService.validateRole(roleId);
            result = alarmService.getAlarmList(role);

        } catch (IllegalArgumentException ex) {
            res.put("message", ex.getMessage());
            res.put("status_code", 400);
            return ResponseEntity.badRequest().body(res);
        }

        res.put("data", result);
        return ResponseEntity.ok(res);
    }
}
