package com.sidam_backend.service;

import com.sidam_backend.data.*;
import com.sidam_backend.repo.*;

import com.sidam_backend.service.base.UsingAlarmService;
import com.sidam_backend.service.base.Validation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Service
public class ChangeService extends UsingAlarmService implements Validation {

    public ChangeService(
            ChangeRequestRepository changeRequestRepository,
            StoreRepository storeRepository,
            AccountRoleRepository accountRoleRepository,
            DailyScheduleRepository dailyScheduleRepository,
            AlarmRepository alarmRepository,
            AlarmReceiverRepository receiverRepository
    ) {
        super(alarmRepository, accountRoleRepository, receiverRepository);
        this.changeRequestRepository = changeRequestRepository;
        this.dailyScheduleRepository = dailyScheduleRepository;
        this.accountRoleRepository = accountRoleRepository;
        this.storeRepository = storeRepository;
    }

    private final ChangeRequestRepository changeRequestRepository;
    private final StoreRepository storeRepository;
    private final AccountRoleRepository accountRoleRepository;
    private final DailyScheduleRepository dailyScheduleRepository;

    public void validateDate(LocalDate date) {
        if (LocalDate.now().isAfter(date)) {
            throw new IllegalArgumentException("지난 근무는 바꿀 수 없습니다.");
        }
    }

    public Store validateStoreId(Long storeId) {

        return storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException(storeId + " store is not exist."));
    }

    public AccountRole validateRoleId(Long roleId) {

        return accountRoleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException(roleId + " role is not exist."));
    }

    @Override
    public Account validateAccount(Long accountId) {
        return null;
    }

    public DailySchedule validateSchedule(Long scheduleId) {

        return dailyScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException(scheduleId + " schedule is not exist."));
    }

    public void validateWorker(DailySchedule schedule, AccountRole role) {

        if (!schedule.getUsers().contains(role)) {
            throw new IllegalArgumentException("스케줄에 " + role.getId() + " 근무자가 존재하지 않음");
        }
    }

    public ChangeRequest saveChangeRequest(Long roleId, Long schedule) {

        ChangeRequest request = new ChangeRequest();

        request.setRequester(roleId);
        request.setDate(LocalDateTime.now().withNano(0));
        request.setOldSchedule(schedule);
        request.setOwnState(ChangeRequest.State.NON);
        request.setResState(ChangeRequest.State.INVALID);

        changeRequestRepository.save(request);

        return changeRequestRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException(request.getId() + " save fails"));
    }

    public ChangeRequest saveChangeRequest(Long roleId, Long targetId, Long schedule, Long target) {

        ChangeRequest request = new ChangeRequest();

        request.setRequester(roleId);
        request.setReceiver(targetId);
        request.setDate(LocalDateTime.now());
        request.setOldSchedule(schedule);
        request.setTargetSchedule(target);
        request.setOwnState(ChangeRequest.State.NON);
        request.setResState(ChangeRequest.State.NON);

        changeRequestRepository.save(request);

        return changeRequestRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException(request.getId() + " save fails"));
    }
}
