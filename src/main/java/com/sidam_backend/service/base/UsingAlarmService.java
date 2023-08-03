package com.sidam_backend.service.base;

import com.sidam_backend.data.*;
import com.sidam_backend.repo.AlarmReceiverRepository;
import com.sidam_backend.repo.AlarmRepository;
import com.sidam_backend.repo.AccountRoleRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class UsingAlarmService {

    // 알림 생성기
    private final AlarmRepository alarmRepository;
    private final AccountRoleRepository accountRoleRepository;
    private final AlarmReceiverRepository receiverRepository;

    // 알림 생성기 (직원 대상)
    public void employeeAlarmMaker(Store store, String title, Alarm.Category cate, Alarm.State state, Long cid) {

        List<AccountRole> roles = accountRoleRepository.findEmployees(store.getId());
        List<AlarmReceiver> receivers = new ArrayList<>();

        Alarm alarm = new Alarm(cate, title, state, cid);

        for (AccountRole role : roles) {
            AlarmReceiver receiver = new AlarmReceiver(alarm, role);
            receivers.add(receiver);
        }
        alarmRepository.save(alarm);
        receiverRepository.saveAll(receivers);
    }

    // 알림 생성기 (사장 대상)
    public void managerAlarmMaker(Store store, String title, Alarm.Category cate, Alarm.State state, Long cid) {

        AccountRole manager = accountRoleRepository.findOwner(store.getId())
                .orElseThrow(() -> new IllegalArgumentException(store.getId() + "store owner not found."));

        Alarm alarm = new Alarm(cate, title, state, cid);
        AlarmReceiver alarmReceiver = new AlarmReceiver(alarm, manager);

        alarmRepository.save(alarm);
        receiverRepository.save(alarmReceiver);
    }

    // 알림 생성기 (근무 교환)
    public void changeAlarmMaker(Store store, ChangeRequest req) {

        AccountRole manager = accountRoleRepository.findOwner(store.getId())
                .orElseThrow(() -> new IllegalArgumentException(store.getId() + "store owner not found."));
        AccountRole receiver = null;

        if (req.getReceiver() != null) {
            receiver = accountRoleRepository.findByIdAndStore(req.getReceiver(), store)
                    .orElseThrow(() -> new IllegalArgumentException(
                            req.getReceiver() + " user not found from " + store.getId() + " store."));
        }

        // 알림 저장
        Alarm alarm = new Alarm(req);
        alarmRepository.save(alarm);

        // 점주 알림 저장
        AlarmReceiver managerReceive = new AlarmReceiver(alarm, manager);
        receiverRepository.save(managerReceive);

        // 교환 대상 알림 저장
        if (receiver != null){
            AlarmReceiver targetReceive = new AlarmReceiver(alarm, receiver);
            receiverRepository.save(targetReceive);
        }
    }

    public String formattingDate(LocalDateTime start, LocalDateTime end) {

        return start.format(DateTimeFormatter.ofPattern("M월 dd일"))
                + "-" + end.format(DateTimeFormatter.ofPattern("M월 dd일"));
    }
}
