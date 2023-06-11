package com.sidam_backend.service;

import com.sidam_backend.data.AbleTime;
import com.sidam_backend.data.DailySchedule;
import com.sidam_backend.data.Store;
import com.sidam_backend.data.UserRole;
import com.sidam_backend.repo.AbleTimeRepository;
import com.sidam_backend.repo.DailyScheduleRepository;
import com.sidam_backend.repo.StoreRepository;
import com.sidam_backend.repo.UserRoleRepository;
import com.sidam_backend.resources.ImpossibleTime;
import com.sidam_backend.resources.ImpossibleTimes;
import com.sidam_backend.resources.Schedule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final DailyScheduleRepository scheduleRepository;
    private final StoreRepository storeRepository;
    private final UserRoleRepository userRoleRepository;
    private final AbleTimeRepository ableTimeRepository;

    private LocalDate validateDate(int year, int month, int day) {

        LocalDate date = LocalDate.of(year, month, day);
        DayOfWeek week = date.getDayOfWeek();

        if (week.getValue() != 1) {
            throw new IllegalArgumentException("시작일이 월요일이 아닙니다.");
        }

        return date;
    }

    public Store validateStoreId(Long storeId) {

        return storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException(storeId + "는 존재하지 않는 매장입니다."));
    }

    public UserRole validateRoleId(Long roleId) {

        return userRoleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException(roleId + "는 존재하지 않는 사용자입니다."));
    }

    public DailySchedule[] getWeeklySchedule(Store store, int year, int month, int day) {

        DailySchedule[] dailySchedules = new DailySchedule[7];

        LocalDate date = LocalDate.of(year, month, day);
        YearMonth mm = YearMonth.from(date);
        int add = 0;

        for (int i = 0; i < 7; i++) {

            if (day + add > mm.atEndOfMonth().getDayOfMonth()) {
                month++;
                day = 1;
                add = 0;
            }

            date = LocalDate.of(year, month, day + add++);
            dailySchedules[i] = scheduleRepository.findByDateAndStore(date, store);
        }

        return dailySchedules;
    }

    public DailySchedule getSchedule(Store store, int year, int month, int day) {

        LocalDate date = LocalDate.of(year, month, day);
        return scheduleRepository.findByDateAndStore(date, store);
    }

    public void saveSchedule(DailySchedule[] schedules) {

        scheduleRepository.saveAll(Arrays.asList(schedules));

        for(DailySchedule ds : schedules) {
            scheduleRepository.findById(ds.getId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "항목 " + ds.getId() + " 저장 실패"));
        }
    }

    public DailySchedule[] toDailySchedule(Store store, Schedule input) {

        DailySchedule[] schedules = new DailySchedule[input.getDate().size()];

        for(int i = 0; i < schedules.length; i++) {

            schedules[i] = new DailySchedule();

            schedules[i].setDate(input.getDate().get(i).getDay());
            schedules[i].setTime(input.getDate().get(i).getTime());
            schedules[i].setStore(store);

            ArrayList<UserRole> workers = new ArrayList<>();
            for(Long id : input.getDate().get(i).getWorkers()) {
                workers.add(
                        userRoleRepository.findByIdAndStore(id, store)
                                .orElseThrow(() -> new IllegalArgumentException(
                                        id + "는 존재하지 않는 근무자입니다."))
                );
            }
            schedules[i].setUsers(workers);

            schedules[i].setVersion(input.getTimeStamp());
        }

        return schedules;
    }

    public AbleTime[] toAbleTime(Store store, UserRole role, ImpossibleTimes data) {

        AbleTime[] ableTime = new AbleTime[data.getData().size()];

        for (int i = 0; i < ableTime.length; i++) {

            ableTime[i] = new AbleTime();

            ableTime[i].setDate(data.getData().get(i).getDate());
            ableTime[i].setTime(data.getData().get(i).getTime());
            ableTime[i].setStore(store);
            ableTime[i].setUser(role);
        }

        return ableTime;
    }

    public void saveAbleTime(AbleTime[] ableTime) {

        log.info("저장 " + Arrays.toString(ableTime));
        ableTimeRepository.saveAll(Arrays.asList(ableTime));

        //for(AbleTime at : ableTime) {
        //    scheduleRepository.findById(at.getId())
        //            .orElseThrow(() -> new IllegalArgumentException(
        //                    "항목 " + at.getId() + " 저장 실패"));
        //}
    }

    public AbleTime getAbleTime(Store store, UserRole userRole,
                                            int year, int month, int day) {

        return ableTimeRepository.findByStoreAndUserAndDate(store, userRole, LocalDate.of(year, month, day));
    }
}
