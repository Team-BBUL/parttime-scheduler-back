package com.sidam_backend.controller;

import com.sidam_backend.data.Alarm;
import com.sidam_backend.data.AlarmReceiver;
import com.sidam_backend.data.AccountRole;
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

    /*
    // 근무 전 알림 조회
    @GetMapping("/work/{id}")
    public ResponseEntity<Map<String, Object>> getBeforeAlarm(
            @PathVariable("id") Long roleId
    ) {

        Map<String, Object> response = new HashMap<>();

        log.info("get before work alarms : " + roleId);

        List<Integer> alarms;
        AccountRole role;
        try {
            role = alarmService.validateRoleId(roleId);
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

        AccountRole role;
        try {
            role = alarmService.validateRoleId(roleId);
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
    */

    // 알림 목록 조회
    @GetMapping("/list/{roleId}")
    public ResponseEntity<Map<String, Object>> getAlarmList(
            @PathVariable Long roleId,
            //@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime date,
            @RequestParam(name = "page") int pageNum
    ) {
        Map<String, Object> res = new HashMap<>();

        log.info("get alarm list: id " + roleId);

        AccountRole role;
        List<GetAlarm> result;
        try {
            role = alarmService.validateRoleId(roleId);
            result = alarmService.getAlarmList(role, pageNum);

            res.put("data", result);
            return ResponseEntity.ok(res);

        } catch (IllegalArgumentException ex) {
            res.put("message", ex.getMessage());
            res.put("status_code", 400);
            return ResponseEntity.badRequest().body(res);
        }
    }

    // 근무 교환 알림 승낙/거부
    @PostMapping("/change/{roleId}")
    public ResponseEntity<Map<String, Object>> changeRequestRes(
            @PathVariable Long roleId,
            @RequestParam("id") Long receiveId,
            @RequestParam boolean accept
    ) {
        Map<String, Object> res = new HashMap<>();

        AlarmReceiver alarmReceiver;
        AccountRole requester;

        try {
            requester = alarmService.validateRoleId(roleId);
            alarmReceiver = alarmService.validateReceive(receiveId);

            if (alarmReceiver.getAlarm().getType() != Alarm.Category.CHANGE) {
                throw new IllegalArgumentException("잘못된 receiver ID: " + receiveId);
            }

            // 이 때 accept가 false이면 따로 push 알림을 줘야지 않을까?

            int code = alarmService.changeAccept(accept, alarmReceiver, requester);

            res.put("message", accept ? "accept" : "denial" + " successful");
            res.put("status_code", code);

            return ResponseEntity.ok(res);

        } catch (IllegalArgumentException ex) {
            res.put("message", ex.getMessage());
            res.put("status_code", 400);

            return ResponseEntity.badRequest().body(res);
        }
    }

    // 매장 가입 요청 거부/승낙
    @PostMapping("/join/{roleId}")
    public ResponseEntity<Map<String, Object>> joinRequestRes(
            @PathVariable Long roleId,
            @RequestParam("id") Long receiveId,
            @RequestParam boolean accept
    ) {
        Map<String, Object> res = new HashMap<>();

        AlarmReceiver alarmReceiver;
        AccountRole role;

        try {
            role = alarmService.validateRoleId(roleId);
            alarmReceiver = alarmService.validateReceive(receiveId);
            alarmService.joinAccept(accept, alarmReceiver, role);

            if (alarmReceiver.getAlarm().getType() != Alarm.Category.JOIN) {
                throw new IllegalArgumentException("잘못된 receiver ID: " + receiveId);
            }

            // 매장 가입 요청자에게 알림 줘 말어?

        } catch (IllegalArgumentException ex) {
            res.put("message", ex.getMessage());
            res.put("status_code", 400);
            return ResponseEntity.badRequest().body(res);
        }

        res.put("message", accept ? "accept" : "denial" + " successful");
        res.put("status_code", 200);
        return ResponseEntity.ok(res);
    }

    // 알림 삭제
    @DeleteMapping("/list/{roleId}")
    public ResponseEntity<Map<String, Object>> deleteAlarm(
            @PathVariable Long roleId,
            @RequestParam("id") Long alarmId
    ) {
        Map<String, Object> res = new HashMap<>();

        AlarmReceiver alarm;
        AccountRole role;

        try {
            role = alarmService.validateRoleId(roleId);
            alarm = alarmService.validateReceive(alarmId);

            if (alarm.getAccountRole().equals(role)) {
                alarmService.deleteReceiver(alarm);
            } else {
                throw new IllegalArgumentException(alarm.getId() + " 알림은 role" + role.getId() + "의 알림이 아닙니다.");
            }
        } catch (IllegalArgumentException ex) {
            res.put("statusCode", 400);
            res.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(res);
        }

        res.put("statusCode", 200);
        res.put("message", "delete successful");

        return ResponseEntity.ok(res);
    }
}
