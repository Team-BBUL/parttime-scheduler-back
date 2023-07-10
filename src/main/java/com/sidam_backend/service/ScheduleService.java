package com.sidam_backend.service;

import com.sidam_backend.data.AbleTime;
import com.sidam_backend.data.DailySchedule;
import com.sidam_backend.data.Store;
import com.sidam_backend.data.UserRole;
import com.sidam_backend.repo.AbleTimeRepository;
import com.sidam_backend.repo.DailyScheduleRepository;
import com.sidam_backend.repo.StoreRepository;
import com.sidam_backend.repo.UserRoleRepository;
import com.sidam_backend.resources.*;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

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
            throw new IllegalArgumentException("invalidation date");
        }

        return date;
    }

    public Store validateStoreId(Long storeId) {

        return storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException(storeId + " store is not exist."));
    }

    public UserRole validateRoleId(Long roleId) {

        return userRoleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException(roleId + " role is not exist."));
    }

    public List<DailySchedule> getWeeklySchedule(Store store, int year, int month, int day) {

        List<DailySchedule> dailySchedules = new ArrayList<>();

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
            dailySchedules.addAll(scheduleRepository.findAllByDateAndStore(date, store));
        }

        return dailySchedules;
    }

    public List<DailySchedule> getSchedule(Store store, int year, int month, int day) {

        LocalDate date = LocalDate.of(year, month, day);
        return scheduleRepository.findAllByDateAndStore(date, store);
    }

    public void saveSchedule(DailySchedule[] schedules) {

        scheduleRepository.saveAll(Arrays.asList(schedules));

        for(DailySchedule ds : schedules) {
            scheduleRepository.findById(ds.getId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "data " + ds.getId() + " save fails."));
        }
    }

    public void deleteWeeklySchedule(List<DailySchedule> schedules) {

        scheduleRepository.deleteAll(schedules);
    }

    @Transactional
    public void updateSchedule(UpdateSchedule schedule, Store store) {

        for (GetDaily pd : schedule.getDate()) {

            DailySchedule oldSchedule = scheduleRepository.findById(pd.getId())
                    .orElseThrow(() -> new IllegalArgumentException(pd.getId() + " schedule is not exist."));

            oldSchedule.setTime(pd.getTime());
            oldSchedule.setVersion(schedule.getTimeStamp());

            List<UserRole> users = new ArrayList<>();
            for (Worker worker : pd.getWorkers()) {
                users.add(userRoleRepository.findByIdAndStore(worker.getId(), store)
                        .orElseThrow(() -> new IllegalArgumentException(
                                worker.getId() + " userRole is not exist."
                        )));
            }

            oldSchedule.setUsers(users);
        }
    }

    public DailySchedule[] toDailySchedule(Store store, PostSchedule input) {

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
                                        id + " userRole is not exist."))
                );
            }
            schedules[i].setUsers(workers);

            schedules[i].setVersion(input.getTimeStamp());
        }

        return schedules;
    }

    public AbleTime[] toAbleTime(Store store, UserRole role, PostImpossibleTime data) {

        AbleTime[] ableTime = new AbleTime[data.getData().size()];

        for (int i = 0; i < ableTime.length; i++) {

            ableTime[i] = new AbleTime();

            ableTime[i].setDate(data.getData().get(i).getDate());
            ableTime[i].setTime(data.getData().get(i).getTime());
            ableTime[i].setStore(store);
            ableTime[i].setUserRole(role);
        }

        return ableTime;
    }

    public void saveAbleTime(AbleTime[] ableTime) {

        log.info("save " + Arrays.toString(ableTime));
        ableTimeRepository.saveAll(Arrays.asList(ableTime));
    }

    @Transactional
    public void updateAbleTime(PostImpossibleTime input) {

        for (ImpossibleTime time : input.getData()) {
            AbleTime oldTime = ableTimeRepository.findById(time.getId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            time.getId() + " able time is not exist"
                    ));

            oldTime.setTime(time.getTime());
        }
    }

    public ImpossibleTime[] getAbleTimes(Store store, UserRole userRole,
                                            int year, int month, int day) {

        ImpossibleTime[] impossibleTimes = new ImpossibleTime[7];
        AbleTime[] ableTimes = new AbleTime[7];

        LocalDate date = LocalDate.of(year, month, day);
        YearMonth mm = YearMonth.from(date);
        int add = 0;
        int nullSize = 0;

        for (int i = 0; i < 7; i++) {

            if (day + add > mm.atEndOfMonth().getDayOfMonth()) {
                month++;
                day = 1;
                add = 0;
            }

            date = LocalDate.of(year, month, day + add++);
            ableTimes[i] = ableTimeRepository.findByStoreAndUserRoleAndDate(store, userRole, date);

            if (ableTimes[i] != null) {
                impossibleTimes[i] = ableTimes[i].toImpossibleTime();
            } else {
                nullSize++;
            }
        }

        if (nullSize >= 7) {
            impossibleTimes = null;
        }

        return impossibleTimes;
    }

    public void deleteAbleTimes(Store store, UserRole role, int year, int month, int day) {

        List<AbleTime> ableTimes = new ArrayList<>();

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
            ableTimes.add(ableTimeRepository.findByStoreAndUserRoleAndDate(store, role, date));
        }

        ableTimeRepository.deleteAll(ableTimes);
    }
}
