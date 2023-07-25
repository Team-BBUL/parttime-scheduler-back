package com.sidam_backend.utility;

import com.sidam_backend.data.AbleTime;
import com.sidam_backend.data.AccountRole;
import com.sidam_backend.data.DailySchedule;
import com.sidam_backend.data.Store;
import com.sidam_backend.repo.AbleTimeRepository;
import com.sidam_backend.repo.AccountRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
public class AutoMaker {

    private final AccountRoleRepository accountRoleRepository;
    private final AbleTimeRepository ableTimeRepository;

    // 전달된 날짜의 일주일 전 날짜 구하기
    public LocalDate lastWeek(int year, int month, int day) {

        int dd = day - 7, mm = month, yy = year;

        if (dd < 1) {
            mm--;
        }

        if (mm < 1) {
            mm += 12;
            yy--;
        }

        if (dd < 1) {
            int last = LocalDate.of(yy, mm, 1).lengthOfMonth();
            dd = last - (7 - day);
        }

        return LocalDate.of(yy, mm, dd);
    }

    private int getWorkTime(DailySchedule schedule) {

        int i = 0;
        for (boolean time : schedule.getTime()) {
            if (time) { i++; }
        }

        return i;
    }

    // 각 근무자의 주간 근무 시간을 가져옴
    private Map<AccountRole, Integer> sumTotalTime(List<DailySchedule> weekly) {

        Map<AccountRole, Integer> workTime = new HashMap<>();

        for (DailySchedule schedule : weekly) {

            // schedule이 몇 시간의 근무인지 확인
            int time = 0;
            for (boolean t : schedule.getTime()) {
                if (t) { time++; }
            }

            // 주간 근무 시간을 구하기 위해 각 근무자의 근무 시간을 더함
            for (AccountRole role : schedule.getUsers()) {

                if (!role.isValid() || role.getId() == null) { continue; }

                int base = workTime.getOrDefault(role, 0);
                workTime.put(role, base + time);
            }
        }

        for (Map.Entry<AccountRole, Integer> item : workTime.entrySet()) {
            log.debug(item.getKey().getAlias() + ": " + item.getValue() + "시간");
        }

        return workTime;
    }

    // worker가 schedule이 불가능 시간과 겹치는지 확인하는 메소드
    private boolean ableCheck (DailySchedule schedule, Store store, AccountRole worker){ // 불가능 시간과 schedule이 ruqclaus false, 안 겹치면 true

        AbleTime ableTime = ableTimeRepository.
                findByStoreAndAccountRoleAndDate(store, worker, schedule.getDate());

        if (ableTime == null) {
            return true;
        }

        for (int i = 0; i < schedule.getTime().size(); i++) {

            if (ableTime.getTime().get(i) && schedule.getTime().get(i)) {
                return false;
            }
        }

        return true;
    }

    // schedule에 들어갈 (근무 불가능 시간에 걸리지 않고 schedule 날짜에 근무가 없는 경우)
    // level-1 이상에 해당하는 가능한 모든 근무자 return
    private List<AccountRole> substitute(List<DailySchedule> weekly, DailySchedule schedule, int level, Store store) {

        List<AccountRole> result = new ArrayList<>();
        List<AccountRole> workers = accountRoleRepository.findEmployeesOverLevel(store.getId(), level);
        // level-1 이상의 level을 가진 전체 근무자 조회

        // 레벨이 높은 순(내림차순)으로 정렬해서 높은 레벨의 근무자부터 조회
        Comparator<AccountRole> levelComp = Comparator.comparingInt(AccountRole::getLevel).reversed()
                .thenComparing(AccountRole::getCost);
        workers.sort(levelComp);

        for (AccountRole worker : workers) {

            // worker가 불가능 시간에 걸리지 않았고, schedule에 없을 경우 result에 추가
            if (ableCheck(schedule, store, worker)
                    && !schedule.getUsers().contains(worker)) {
                result.add(worker);
            }
        }

        // 같은 날 근무가 있는 근무자 제외
        List<AccountRole> removed = new ArrayList<>();
        for (DailySchedule day : weekly) {

            if (day.getDate().equals(schedule.getDate())) {
                removed.addAll(day.getUsers());
            }
        }

        result.removeAll(removed);

        log.debug("대체 가능 근무자");
        for (AccountRole role : result) {
            log.debug(role.getAlias() + " = lv" + role.getLevel() + " cost=" + role.getCost());
        }

        return result;
    }

    // pastSchedule에서 근무 시간과 근무 불가능 시간이 겹치는 근무자 삭제
    public List<DailySchedule> deleteWorker(List<DailySchedule> pastSchedule, Store store) {

        List<DailySchedule> result = new ArrayList<>();

        for (DailySchedule schedule : pastSchedule) {
            List<AccountRole> workers = schedule.getUsers();

            // 각 근무자 한 명 한 명의 불가능 시간을 확인해서 스케줄에서 삭제
            for (int i = workers.size() - 1; i >= 0; i--) {

                // 근무 시간과 불가능 시간이 겹치는지 확인, 겹치면 삭제하고 dummy 추가
                // dummy = valid field가 false이고, level은 삭제된 근무자의 level을 가짐
                if (!ableCheck(schedule, store, workers.get(i))) {
                    AccountRole dummy = new AccountRole();
                    dummy.setValid(false);
                    dummy.setLevel(workers.get(i).getLevel());

                    schedule.getUsers().remove(workers.get(i));
                    schedule.getUsers().add(dummy);
                }
            }
            result.add(schedule);
        }

        return result;
    }

    // schedule에 들어갈 최적 근무자 찾기
    public AccountRole fitting(List<DailySchedule> weekly, DailySchedule schedule, Store store, int level) {

        AccountRole result = new AccountRole(); // 반환

        int workTime = getWorkTime(schedule); // 바꿔야할 근무의 근무 시간 계산
        boolean second = false, alternative = false; // 등급 조건
        Map<AccountRole, Integer> totalTime = sumTotalTime(weekly); // 각 근무자의 주간 근로 시간 계산

        List<AccountRole> subWorkers;
        subWorkers = substitute(weekly, schedule, level, store); // 대체 가능한 근무자 List 찾아오기

        // 대체 근무자 리스트(subWorkers)를 확인해서 찾음
        for (AccountRole sub : subWorkers) {

            // first = 주 15시간 미만, 레벨 일치 근무자 = 최적해
            if (totalTime.get(sub) < (14 - workTime) && sub.getLevel() == level) {
                return sub;
            }

            // second = 주 15시간 미만, 레벨 불일치 근무자
            else if (totalTime.get(sub) < (14 - workTime)) {
                second = true;
                result = sub;
            }

            // alternative = 주 15시간 이상, 레벨 일치 근무자
            else if (sub.getLevel() == level && !second) {
                alternative = true;
                result = sub;
            }

            // worst = 주 15시간 이상, 레벨 불일치 근무자
            else if (!second && !alternative) {
                result = sub;
            }
        }

        return result;
    }
}
