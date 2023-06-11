package com.sidam_backend.controller;

import com.sidam_backend.data.AbleTime;
import com.sidam_backend.data.DailySchedule;
import com.sidam_backend.data.Store;
import com.sidam_backend.data.UserRole;
import com.sidam_backend.resources.ImpossibleTime;
import com.sidam_backend.resources.ImpossibleTimes;
import com.sidam_backend.resources.PostDaily;
import com.sidam_backend.resources.Schedule;
import com.sidam_backend.service.ScheduleService;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<String> nonValidated(ValidationException ex) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body("근무표가 작성되지 않은 날짜의 조회 " + ex.getMessage());
    }

    // 근무표 일주일 단위 조회
    @GetMapping("schedule/{storeId}")
    public ResponseEntity<Map<String, Object>> getWeeklySchedule(
            @PathVariable Long storeId,
            @RequestParam("version") LocalDateTime ver,
            @RequestParam("year") int year,
            @RequestParam("month") int month,
            @RequestParam("day") int day
    ) {
        Map<String, Object> response = new HashMap<>();

        log.info("일주일 근무표 조회: Store" + storeId + " ver" + ver +
                " " + year + "." + month + "." + day);

        Store store;

        try {
            store = scheduleService.validateStoreId(storeId);
        } catch (IllegalArgumentException ex) {
            response.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        DailySchedule[] schedules;
        try {
            schedules = scheduleService.getWeeklySchedule(store, year, month, day);
        } catch (IllegalArgumentException ex) {
            response.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        LocalDateTime version = schedules[0].getVersion();

        if (schedules[0].getVersion() == ver) {
            response.put("time_stamp", ver);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        }

        // 전송형태로 변환
        PostDaily[] postDaily = new PostDaily[7];
        for (int i = 0; i < 7; i++) {
            if (schedules[i] != null) {
                postDaily[i] = schedules[i].toDaily();
            }
        }

        response.put("time_stamp", version);
        response.put("date", postDaily);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/daily-schedule/{storeId}")
    public ResponseEntity<Map<String, Object>> getSchedule (
            @PathVariable Long storeId,
            @RequestParam("version") LocalDateTime ver,
            @RequestParam("year") int year,
            @RequestParam("month") int month,
            @RequestParam("day") int day) {

        Map<String, Object> response = new HashMap<>();

        // log 저장하는 방법
        log.info("근무표 조회: Store" + storeId + " ver" + ver +
                " " + year + "." + month + "." + day);

        Store store;

        try {
            store = scheduleService.validateStoreId(storeId);
        } catch (IllegalArgumentException ex) {
            response.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        DailySchedule schedule = scheduleService.getSchedule(store, year, month, day);

        if (schedule == null) {
            response.put("status_code", 204);
            response.put("data", "근무표가 작성되지 않은 날짜의 조회 " + year + "." + month + "." + day);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        }

        response.put("time_stamp", schedule.getVersion());
        response.put("data", schedule.toDaily());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/schedule/{storeId}")
    public ResponseEntity<Map<String, Object>> postSchedule(
            @PathVariable Long storeId,
            @RequestBody Schedule schedule) {

        Map<String, Object> result = new HashMap<>();

        log.info("근무표 편성: Store" + storeId + " ver" + schedule.getTimeStamp());

        Store store;

        try {
            store = scheduleService.validateStoreId(storeId);
        } catch (IllegalArgumentException ex) {
            result.put("status_code", 400);
            result.put("data", storeId + "는 존재하지 않는 매장입니다");
            return ResponseEntity.badRequest().body(result);
        }

        log.info("근무표 저장 " + schedule);

        DailySchedule[] weekly;
        try {
            weekly = scheduleService.toDailySchedule(store, schedule);
        } catch (IllegalArgumentException ex) {
            result.put("status_code", 400);
            result.put("data", "입력 오류: " + ex.getMessage());
            return ResponseEntity.badRequest().body(result);
        }

        try {
            scheduleService.saveSchedule(weekly);
        } catch (IllegalArgumentException ex) {
            result.put("status_code", 400);
            result.put("data", "저장 오류: " + ex.getMessage());
            return ResponseEntity.badRequest().body(result);
        }

        result.put("status_code", 201);
        result.put("data", "저장 성공");
        return ResponseEntity.ok(result);
    }

    @PostMapping("/schedule/impossible/{storeId}")
    public ResponseEntity<Map<String, Object>> chooseImpossible (
            @PathVariable Long storeId,
            @RequestParam(value = "id") Long roleId,
            @RequestBody ImpossibleTimes data) {

        Map<String, Object> response = new HashMap<>();

        log.info("근무 불가능 시간 저장: " + data);

        Store store;
        try {
            store = scheduleService.validateStoreId(storeId);
        } catch (IllegalArgumentException ex) {
            response.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        UserRole userRole;
        try {
            userRole = scheduleService.validateRoleId(roleId);
        } catch (IllegalArgumentException ex) {
            response.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            scheduleService.saveAbleTime(scheduleService.toAbleTime(store, userRole, data));
        } catch (IllegalArgumentException ex) {
            response.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        response.put("status_code", 200);
        response.put("data", "저장 성공");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/schedule/impossible/{storeId}")
    public ResponseEntity<Map<String, Object>> getImpossibleTime(
            @PathVariable Long storeId,
            @RequestParam(value = "id") Long roleId,
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam int day,
            @RequestParam int time) {

        Map<String, Object> response = new HashMap<>();

        Store store;
        try {
            store = scheduleService.validateStoreId(storeId);
        } catch (IllegalArgumentException ex) {
            response.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        UserRole userRole;
        try {
            userRole = scheduleService.validateRoleId(roleId);
        } catch (IllegalArgumentException ex) {
            response.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        AbleTime ableTime;
        try {
            ableTime = scheduleService.getAbleTime(store, userRole, year, month, day);
        } catch (IllegalArgumentException ex) {
            response.put("status_code", 204);
            response.put("message", "불가능 시간이 존재하지 않습니다.");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        }

        if (ableTime == null) {
            response.put("able", true);
        } else {
            response.put("able", ableTime.getTime().get(time));
        }

        return ResponseEntity.ok(response);
    }
}
