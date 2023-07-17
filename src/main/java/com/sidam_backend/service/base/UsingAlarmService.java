package com.sidam_backend.service.base;

import com.sidam_backend.data.Alarm;
import com.sidam_backend.data.AlarmReceiver;
import com.sidam_backend.data.Store;
import com.sidam_backend.data.UserRole;
import com.sidam_backend.repo.AlarmReceiverRepository;
import com.sidam_backend.repo.AlarmRepository;
import com.sidam_backend.repo.UserRoleRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class UsingAlarmService {

    // 알림 생성기
    private final AlarmRepository alarmRepository;
    private final UserRoleRepository userRoleRepository;
    private final AlarmReceiverRepository receiverRepository;

    // 알림 생성기
    public void employeeAlarmMaker(Store store, String title, Alarm.Category cate, Alarm.State state) {

        List<UserRole> roles = userRoleRepository.findEmployees(store.getId());
        List<AlarmReceiver> receivers = new ArrayList<>();

        Alarm alarm = new Alarm(cate, title, state);

        for (UserRole role : roles) {
            AlarmReceiver receiver = new AlarmReceiver(alarm, role);
            receivers.add(receiver);
        }
        alarmRepository.save(alarm);
        receiverRepository.saveAll(receivers);
    }

    public void managerAlarmMaker(Store store, String title, Alarm.Category cate, Alarm.State state) {

        UserRole manager = userRoleRepository.findOwner(store.getId())
                .orElseThrow(() -> new IllegalArgumentException(store.getId() + "store owner not found."));

        Alarm alarm = new Alarm(cate, title, state);
        AlarmReceiver alarmReceiver = new AlarmReceiver(alarm, manager);

        alarmRepository.save(alarm);
        receiverRepository.save(alarmReceiver);
    }

    public String formattingDate(LocalDateTime start, LocalDateTime end) {

        return start.format(DateTimeFormatter.ofPattern("M월 dd일"))
                + "-" + end.format(DateTimeFormatter.ofPattern("M월 dd일"));
    }
}
