package com.sidam_backend.service;

import com.sidam_backend.data.ChangeRequest;
import com.sidam_backend.data.DailySchedule;
import com.sidam_backend.data.Store;
import com.sidam_backend.data.UserRole;
import com.sidam_backend.repo.ChangeRequestRepository;

import com.sidam_backend.repo.DailyScheduleRepository;
import com.sidam_backend.repo.StoreRepository;
import com.sidam_backend.repo.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChangeService {

    private final ChangeRequestRepository changeRequestRepository;
    private final StoreRepository storeRepository;
    private final UserRoleRepository userRoleRepository;
    private final DailyScheduleRepository dailyScheduleRepository;

    public Store validateStoreId(Long storeId) {

        return storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException(storeId + " store is not exist."));
    }

    public UserRole validateRoleId(Long roleId) {

        return userRoleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException(roleId + " role is not exist."));
    }

    public DailySchedule validateSchedule(Long scheduleId) {

        return dailyScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException(scheduleId + " schedule is not exist."));
    }

    public void saveChangeRequest(Long roleId, Long schedule) {

        ChangeRequest request = new ChangeRequest();

        request.setRequester(roleId);
        request.setDate(LocalDateTime.now().withNano(0));
        request.setOldSchedule(schedule);
        request.setOwnState(ChangeRequest.State.NON);
        request.setResState(ChangeRequest.State.NON);

        changeRequestRepository.save(request);

        changeRequestRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException(request.getId() + " save fails"));
    }

    public void saveChangeRequest(Long roleId, Long targetId, Long schedule, Long target) {

        ChangeRequest request = new ChangeRequest();

        request.setRequester(roleId);
        request.setResponser(targetId);
        request.setDate(LocalDateTime.now());
        request.setOldSchedule(schedule);
        request.setTargetSchedule(target);
        request.setOwnState(ChangeRequest.State.NON);
        request.setResState(ChangeRequest.State.NON);

        changeRequestRepository.save(request);

        changeRequestRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException(request.getId() + " save fails"));
    }
}
