package com.sidam_backend.controller;

import com.sidam_backend.data.UserRole;
import com.sidam_backend.data.WorkAlarm;
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
            alarms = alarmService.getAlarm(role);
        } catch (IllegalArgumentException ex) {
            response.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(response);
        }

        response.put("times", alarms);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/work/{id}")
    public ResponseEntity<Map<String, Object>> postBeforeAlarm(
            @PathVariable("id") Long roleId,
            @RequestParam("term") int term
    ) {

        Map<String, Object> response = new HashMap<>();

        log.info("set before work alarm : before " + term + "m of " + roleId);

        UserRole role;
        try {
            role = alarmService.validateRole(roleId);
            alarmService.saveAlarm(term, role);
        } catch (IllegalArgumentException ex) {
            response.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(response);
        }

        response.put("message", "save success");
        return ResponseEntity.ok(response);
    }
}
