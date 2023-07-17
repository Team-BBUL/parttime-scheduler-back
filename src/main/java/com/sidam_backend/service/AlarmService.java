package com.sidam_backend.service;

import com.sidam_backend.data.Alarm;
import com.sidam_backend.data.AlarmReceiver;
import com.sidam_backend.data.UserRole;
import com.sidam_backend.data.WorkAlarm;
import com.sidam_backend.repo.AlarmReceiverRepository;
import com.sidam_backend.repo.AlarmRepository;
import com.sidam_backend.repo.UserRoleRepository;
import com.sidam_backend.repo.WorkAlarmRepository;
import com.sidam_backend.resources.DTO.GetAlarm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmService {

    private final UserRoleRepository userRoleRepository;
    private final WorkAlarmRepository workAlarmRepository;
    private final AlarmRepository alarmRepository;
    private final AlarmReceiverRepository receiverRepository;

    // term list 중에서 base와 같은 시간 찾기
    private boolean findTime(int base, List<Integer> term) {

        for (int i : term) {
            if (base == i) {
                return true;
            }
        }

        return false;
    }

    // 사용자 유효성 확인
    public UserRole validateRole(Long roleId) {

        return userRoleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException(roleId + "is not valid."));
    }

    // 근무 시작 전 알림 가져오기
    public List<Integer> getWorkAlarm(UserRole role) {

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

    // 근무 시작 전 알림 수정하기
    public void updateWorkAlarm(List<Integer> term, UserRole role) {

        List<WorkAlarm> alarms = workAlarmRepository.findAllByUserRole(role); // 기존 DB에 저장된 값
        List<WorkAlarm> delete = new ArrayList<>(); // 삭제할 (term에서 없어진) 값
        List<Integer> existing = new ArrayList<>(); // 기존 DB에 저장된 값 int
        List<WorkAlarm> newData = new ArrayList<>(); // 새로 DB에 저장할 (term에 새로 추가된) 값

        // DB에서 삭제하기 위해 없어진 값 찾기
        for (WorkAlarm alarm : alarms) {
            existing.add(alarm.getTime());
            if (!findTime(alarm.getTime(), term)) {
                delete.add(alarm);
            }
        }
        workAlarmRepository.deleteAll(delete);

        // DB에 추가하기 위해 term에 생긴 값 찾기
        for (int t : term) {
            if (!findTime(t, existing)) {
                WorkAlarm tmp = new WorkAlarm();
                tmp.setUserRole(role);
                tmp.setTime(t);
                newData.add(tmp);
            }
        }
        workAlarmRepository.saveAll(newData);
    }

    // 알림 list 가져오기
    public List<GetAlarm> getAlarmList(UserRole userRole) {

        List<Alarm> alarms = alarmRepository.findCntByUserRole(userRole.getId(), 10);
        List<GetAlarm> result = new ArrayList<>();

        for (Alarm alarm : alarms) {

            GetAlarm tmp = alarm.toGetAlarm();
            AlarmReceiver receiver = receiverRepository.findByUserRoleAndAlarm(userRole, alarm)
                    .orElseThrow(() -> new IllegalArgumentException(
                            alarm.getId() + "alarm to " + userRole.getId() + "user does not exist."
                    ));
            tmp.setRead(receiver.isCheck());

            result.add(tmp);
        }

        return result;
    }
}
