package com.sidam_backend.controller;

import com.sidam_backend.data.*;
import com.sidam_backend.resources.DTO.*;
import com.sidam_backend.service.ScheduleService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    private List<GetDaily> postFormatting(List<DailySchedule> schedules, AccountRole role) {

        // 전송형태로 변환
        List<GetDaily> getDaily = new ArrayList<>();
        for (DailySchedule ds : schedules) {
            if (ds != null) {
                getDaily.add(ds.toDaily(role));
            }
        }

        return getDaily;
    }

    // 근무표 일주일 단위 조회
    @GetMapping("/{storeId}")
    public ResponseEntity<Map<String, Object>> getWeeklySchedule(
            @PathVariable Long storeId,
            @RequestParam("id") Long roleId,
            @RequestParam("version") LocalDateTime ver,
            @RequestParam("year") int year,
            @RequestParam("month") int month,
            @RequestParam("day") int day
    ) {
        Map<String, Object> response = new HashMap<>();

        log.info("get weekly schedule : Store" + storeId + " ver" + ver +
                " " + year + "." + month + "." + day);

        Store store;
        AccountRole role;
        List<DailySchedule> schedules;

        try {
            store = scheduleService.validateStoreId(storeId);
            role = scheduleService.validateRoleId(roleId);
            schedules = scheduleService.getWeeklySchedule(store, year, month, day);

        } catch (IllegalArgumentException ex) {
            response.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        if (schedules.size() == 0) {
            log.info("weekly data not exist " + storeId + " " + year + "." + month + "." + day);
            response.put("status_code", 204);
            response.put("message", "data not exist " + year + "." + month + "." + day);
            return ResponseEntity.ok(response);
        }

        LocalDateTime version = schedules.get(0).getVersion();
        if (version.isEqual(ver)) {
            log.info("same version: " + version);
            response.put("time_stamp", ver);
            return ResponseEntity.ok(response);
        }

        // 전송형태로 변환
        List<GetDaily> getDaily = postFormatting(schedules, role);

        response.put("time_stamp", version);
        response.put("date", getDaily);

        return ResponseEntity.ok(response);
    }

    // 근무표 일단위 조회
    @GetMapping("/daily/{storeId}")
    public ResponseEntity<Map<String, Object>> getSchedule (
            @PathVariable Long storeId,
            @RequestParam("id") Long roleId,
            @RequestParam("version") LocalDateTime ver,
            @RequestParam("year") int year,
            @RequestParam("month") int month,
            @RequestParam("day") int day
    ) {

        Map<String, Object> response = new HashMap<>();

        // log 저장하는 방법
        log.info("get daily schedule: Store" + storeId + " ver" + ver +
                " " + year + "." + month + "." + day);

        Store store;
        AccountRole role;

        try {
            store = scheduleService.validateStoreId(storeId);
            role = scheduleService.validateRoleId(roleId);
        } catch (IllegalArgumentException ex) {
            response.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        List<DailySchedule> schedule = scheduleService.getSchedule(store, year, month, day);

        if (schedule.size() == 0) {
            log.info("daily data not exist " + storeId + " " + year + "." + month + "." + day);
            response.put("status_code", 204);
            response.put("data", "data not exist " + year + "." + month + "." + day);
            return ResponseEntity.ok(response);
        }

        LocalDateTime version = schedule.get(0).getVersion();

        if (version.isEqual(ver)) {
            response.put("time_stamp", version);
            return ResponseEntity.ok(response);
        }

        List<GetDaily> postDailies = postFormatting(schedule, role);

        response.put("time_stamp", version);
        response.put("data", postDailies);
        return ResponseEntity.ok(response);
    }

    // 근무표 작성
    @PostMapping("/{storeId}")
    public ResponseEntity<Map<String, Object>> postSchedule(
            @PathVariable Long storeId,
            @RequestBody PostSchedule schedule) {

        Map<String, Object> result = new HashMap<>();

        log.info("make schedule : Store" + storeId + " ver" + schedule.getTimeStamp());

        Store store;
        List<DailySchedule> weekly;

        try {
            store = scheduleService.validateStoreId(storeId);
            weekly = scheduleService.toDailySchedule(store, schedule);
            scheduleService.saveSchedule(weekly, store);

        } catch (IllegalArgumentException ex) {
            result.put("status_code", 400);
            result.put("data", ex.getMessage());
            return ResponseEntity.badRequest().body(result);
        }

        // 근무표 생성 알림

        result.put("status_code", 201);
        result.put("data", "save success");
        return ResponseEntity.ok(result);
    }

    // 근무표 삭제
    @DeleteMapping("/{storeId}")
    public ResponseEntity<Map<String, Object>> deleteSchedule(
            @PathVariable Long storeId,
            @RequestParam("version") LocalDateTime ver,
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam int day
    ) {
        Map<String, Object> response = new HashMap<>();

        log.info("delete weekly schedule: Store" + storeId + " ver" + ver +
                "  " + year + "." + month + "." + day);

        List<DailySchedule> schedule;
        Store store;

        try {
            store = scheduleService.validateStoreId(storeId);
            schedule = scheduleService.getWeeklySchedule(store, year, month, day);
        } catch (IllegalArgumentException ex) {
            response.put("status_code", 400);
            response.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        if (schedule.size() == 0) {
            response.put("status_code", 400);
            response.put("message", "data not exist");
            return ResponseEntity.ok(response);
        }

        LocalDateTime version = schedule.get(0).getVersion();

        // 삭제 알림에 전달할 문구를 스케줄 시작일부터 종료일로 정하기 위해 정렬함
        schedule.sort(Comparator.comparing(DailySchedule::getDate));
        // 삭제 알림에 전달할 문구 "n월 m일 - n월 m일"
        String dateInfo = scheduleService.formattingDate(schedule.get(0).getDate().atStartOfDay(),
                schedule.get(schedule.size() - 1).getDate().atStartOfDay());

        // 전달받은 버전 정보가 일치하지 않으면 삭제할 수 없게 처리
        if (!version.isEqual(ver)) {
            response.put("status_code", 400);
            response.put("message", ver + " is not the same.");
            response.put("time_stamp", version);
            return ResponseEntity.ok(response);
        }

        try {
            scheduleService.deleteWeeklySchedule(schedule);

        } catch (EmptyResultDataAccessException ex) {
            response.put("status_code", 400);
            response.put("message", "data not exist");
            return ResponseEntity.ok(response);
        }

        // 삭제 알림 저장
        scheduleService.employeeAlarmMaker(store, dateInfo,
                Alarm.Category.SCHEDULE, Alarm.State.DELETE, null);

        response.put("status_code", 200);
        response.put("message", ver + " delete successful");

        return ResponseEntity.ok(response);
    }

    // 근무표 수정
    @PostMapping("/update/{storeId}")
    public ResponseEntity<Map<String, Object>> updateSchedule(
            @PathVariable Long storeId,
            @RequestBody UpdateSchedule schedule
            ) {
        Map<String, Object> response = new HashMap<>();

        log.info("update schedule: store" + storeId + " version " + schedule.getTimeStamp());

        Store store;
        try {
            store = scheduleService.validateStoreId(storeId);
            scheduleService.updateSchedule(schedule, store);
        } catch (IllegalArgumentException ex) {
            response.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(response);
        }

        // 근무표 수정 알림

        response.put("message", "update successful");
        return ResponseEntity.ok(response);
    }

    // 근무 불가능 시간 작성
    @PostMapping("/impossible/{storeId}")
    public ResponseEntity<Map<String, Object>> chooseImpossible (
            @PathVariable Long storeId,
            @RequestParam(value = "id") Long roleId,
            @RequestBody PostImpossibleTime data) {

        Map<String, Object> response = new HashMap<>();

        log.info("impossible time post: " + data.getData());

        Store store;
        AccountRole accountRole;
        try {
            store = scheduleService.validateStoreId(storeId);
            accountRole = scheduleService.validateRoleId(roleId);
            scheduleService.saveAbleTime(scheduleService.toAbleTime(store, accountRole, data));

        } catch (IllegalArgumentException ex) {
            response.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        response.put("message", "save successful");

        return ResponseEntity.ok(response);
    }

    // 특정 근무자의 근무 불가능 시간 조회
    @GetMapping("/impossible/{storeId}")
    public ResponseEntity<Map<String, Object>> getImpossibleTime(
            @PathVariable Long storeId,
            @RequestParam(value = "id") Long roleId,
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam int day) {

        Map<String, Object> response = new HashMap<>();

        log.info("get able time: store" + storeId + " userRole" + roleId
                + " " + year + "." + month + "." + day);

        Store store;
        AccountRole accountRole;
        try {
            store = scheduleService.validateStoreId(storeId);
            accountRole = scheduleService.validateRoleId(roleId);
        } catch (IllegalArgumentException ex) {
            response.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        ImpossibleTime[] ableTime;
        ableTime = scheduleService.getAbleTimes(store, accountRole, year, month, day);

        if (ableTime != null) {
            response.put("data", ableTime);
        }
        else {
            response.put("status_code", 204);
            response.put("message", "data is not exist.");
        }

        return ResponseEntity.ok(response);
    }

    // 근무 불가능 시간 수정
    @PostMapping("/impossible/update/{storeId}")
    public ResponseEntity<Map<String, Object>> updateAbleTime(
            @PathVariable Long storeId,
            @RequestParam("id") Long roleId,
            @RequestBody PostImpossibleTime input
    ) {
        Map<String, Object> response = new HashMap<>();

        try {
            Store store = scheduleService.validateStoreId(storeId);
            AccountRole role = scheduleService.validateRoleId(roleId);
            scheduleService.updateAbleTime(store, role, input);
        } catch (IllegalArgumentException ex) {
            response.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(response);
        }

        response.put("message", "able time update successful");
        return ResponseEntity.ok(response);
    }

    // 근무 불가능 시간 삭제
    @DeleteMapping("/impossible/{storeId}")
    public ResponseEntity<Map<String, Object>> deleteAbleTime(
            @PathVariable Long storeId,
            @RequestParam Long roleId,
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam int day
    ) {
        Map<String, Object> response = new HashMap<>();

        AccountRole role;
        Store store;

        try {
            store = scheduleService.validateStoreId(storeId);
            role = scheduleService.validateRoleId(roleId);

        } catch (IllegalArgumentException ex) {
            response.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            scheduleService.deleteAbleTimes(store, role, year, month, day);
        } catch (EmptyResultDataAccessException ex) {
            response.put("message", "data not exist");
        }

        response.put("message", "delete successful");
        return ResponseEntity.ok(response);
    }

    // 자동편성
    @GetMapping("/make/{storeId}")
    public ResponseEntity<Map<String, Object>> autoMake(
            @PathVariable Long storeId,
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam int day
    ) {
        Map<String, Object> res = new HashMap<>();

        Store store;
        List<GetDaily> dailyList;

        log.info(storeId + " store, auto make");

        try {
            store = scheduleService.validateStoreId(storeId);
            dailyList = scheduleService.autoMake(store, year, month, day);

        } catch (IllegalArgumentException ex) {

            res.put("message", ex.getMessage());
            res.put("status_code", 400);

            return ResponseEntity.badRequest().body(res);
        }

        res.put("date", dailyList);
        return ResponseEntity.ok(res);
    }
}
