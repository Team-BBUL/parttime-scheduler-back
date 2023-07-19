package com.sidam_backend.service;

import com.sidam_backend.data.AccountRole;
import com.sidam_backend.data.WorkAlarm;
import com.sidam_backend.repo.AccountRoleRepository;
import com.sidam_backend.repo.WorkAlarmRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmService {

    private final AccountRoleRepository userRoleRepository;
    private final WorkAlarmRepository workAlarmRepository;

    public AccountRole validateRole(Long roleId) {

        return userRoleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException(roleId + "is not valid."));
    }

    public List<Integer> getAlarm(AccountRole role) {

        List<WorkAlarm> alarms = workAlarmRepository.findAllByUserRole(role);
        List<Integer> result = new ArrayList<>();

        for (WorkAlarm times : alarms) {
            result.add(times.getTime());
        }

        if (alarms.size() == 0) {
            throw new IllegalArgumentException("no data");
        }

        return result;
    }

    public void saveAlarm(int time, AccountRole role) {

        WorkAlarm search = workAlarmRepository.findByTimeAndUserRole(time, role);

        if (search != null) {
            throw new IllegalArgumentException(time + "is already exist.");
        }

        WorkAlarm alarm = new WorkAlarm();

        alarm.setTime(time);
        alarm.setUserRole(role);

        workAlarmRepository.findById(alarm.getId())
                .orElseThrow(() -> new IllegalArgumentException(alarm.getId() + " save false"));
    }
}
