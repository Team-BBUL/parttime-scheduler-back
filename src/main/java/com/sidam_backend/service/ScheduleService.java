package com.sidam_backend.service;

import com.sidam_backend.data.*;
import com.sidam_backend.repo.*;

import com.sidam_backend.resources.DTO.*;
import com.sidam_backend.service.base.UsingAlarmService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@Slf4j
@Service
public class ScheduleService extends UsingAlarmService {

    public ScheduleService(
            DailyScheduleRepository scheduleRepository,
            StoreRepository storeRepository,
            AccountRoleRepository accountRoleRepository,
            AbleTimeRepository ableTimeRepository,
            AlarmRepository alarmRepository,
            AlarmReceiverRepository receiverRepository
    ) {
        super(alarmRepository, accountRoleRepository, receiverRepository);
        this.accountRoleRepository = accountRoleRepository;
        this.scheduleRepository = scheduleRepository;
        this.ableTimeRepository = ableTimeRepository;
        this.storeRepository = storeRepository;
    }

    private final DailyScheduleRepository scheduleRepository;
    private final StoreRepository storeRepository;
    private final AccountRoleRepository accountRoleRepository;
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

    public AccountRole validateRoleId(Long roleId) {

        return accountRoleRepository.findById(roleId)
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

    public void saveSchedule(List<DailySchedule> schedules, Store store) {

        scheduleRepository.saveAll(schedules);

        for(DailySchedule ds : schedules) {
            scheduleRepository.findById(ds.getId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "data " + ds.getId() + " save fails."));
        }

        log.debug("스케줄 저장 완료");
        schedules.sort(Comparator.comparing(DailySchedule::getDate));

        String dateInfo = formattingDate(schedules.get(0).getDate().atStartOfDay(),
                schedules.get(schedules.size() - 1).getDate().atStartOfDay());
        log.debug("스케줄 정렬 및 날짜 formatting 완료 " + dateInfo);
        // 알림 생성
        employeeAlarmMaker(store, dateInfo, Alarm.Category.SCHEDULE, Alarm.State.ADD, schedules.get(0).getId());
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

            List<AccountRole> users = new ArrayList<>();
            for (Worker worker : pd.getWorkers()) {
                users.add(accountRoleRepository.findByIdAndStore(worker.getId(), store)
                        .orElseThrow(() -> new IllegalArgumentException(
                                worker.getId() + " userRole is not exist."
                        )));
            }

            oldSchedule.setUsers(users);
        }

        log.debug("스케줄 업데이트 완료");
        schedule.getDate().sort(Comparator.comparing(GetDaily::getDay));

        String dateInfo = formattingDate(schedule.getDate().get(0).getDay().atStartOfDay(),
                schedule.getDate().get(schedule.getDate().size() - 1).getDay().atStartOfDay());
        log.debug("스케줄 정렬 및 날짜 formatting 완료 " + dateInfo);
        // 알림 생성
        employeeAlarmMaker(store, dateInfo, Alarm.Category.SCHEDULE, Alarm.State.UPDATE,
                schedule.getDate().get(0).getId());
    }

    public List<DailySchedule> toDailySchedule(Store store, PostSchedule input) {

        List<DailySchedule> schedules = new ArrayList<>();

        for(PostDaily daily : input.getData()) {

            DailySchedule schedule = new DailySchedule();

            schedule.setDate(daily.getDay());
            schedule.setTime(daily.getTime());
            schedule.setStore(store);

            ArrayList<AccountRole> workers = new ArrayList<>();
            for(Long id : daily.getWorkers()) {
                workers.add(
                        accountRoleRepository.findByIdAndStore(id, store)
                                .orElseThrow(() -> new IllegalArgumentException(
                                        id + " userRole is not exist."))
                );
            }
            schedule.setUsers(workers);
            schedule.setVersion(input.getTimeStamp());
            schedules.add(schedule);
        }

        return schedules;
    }

    public AbleTime[] toAbleTime(Store store, AccountRole role, PostImpossibleTime data) {

        AbleTime[] ableTime = new AbleTime[data.getData().size()];

        for (int i = 0; i < ableTime.length; i++) {

            ableTime[i] = new AbleTime();

            ableTime[i].setDate(data.getData().get(i).getDate());
            ableTime[i].setTime(data.getData().get(i).getTime());
            ableTime[i].setStore(store);
            ableTime[i].setAccountRole(role);
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

    public ImpossibleTime[] getAbleTimes(Store store, AccountRole accountRole,
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
            ableTimes[i] = ableTimeRepository.findByStoreAndAccountRoleAndDate(store, accountRole, date);

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

    public void deleteAbleTimes(Store store, AccountRole role, int year, int month, int day) {

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
            ableTimes.add(ableTimeRepository.findByStoreAndAccountRoleAndDate(store, role, date));
        }

        ableTimeRepository.deleteAll(ableTimes);
    }
}
