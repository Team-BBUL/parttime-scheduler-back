package com.sidam_backend.service;

import com.sidam_backend.data.*;
import com.sidam_backend.data.enums.Role;
import com.sidam_backend.repo.*;
import com.sidam_backend.resources.DTO.GetAlarm;
import com.sidam_backend.service.base.Validation;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmService implements Validation {

    private final AccountRoleRepository accountRoleRepository;
    private final AlarmReceiverRepository receiverRepository;
    private final StoreRepository storeRepository;
    private final ChangeRequestRepository requestRepository;
    private final DailyScheduleRepository scheduleRepository;

    // term list 중에서 base와 같은 시간 찾기
    private boolean findTime(int base, List<Integer> term) {

        for (int i : term) {
            if (base == i) {
                return true;
            }
        }

        return false;
    }

    private void changeSchedule(ChangeRequest request, AlarmReceiver receiver) {

        // 알림의 상태 변경
        receiver.getAlarm().setState(Alarm.State.ACCEPT);

        // 변경할 스케줄 가져오기
        DailySchedule old = validateSchedule(request.getOldSchedule());
        DailySchedule target = validateSchedule(request.getTargetSchedule());

        // 교환 요청자와 교환 대상자 조회
        AccountRole requester = validateRoleId(request.getRequester());
        AccountRole targetUser = validateRoleId(request.getReceiver());

        // 각 스케줄에 기존 인원 제거
        old.getUsers().remove(requester);
        target.getUsers().remove(targetUser);

        // 각 스케줄에 변경 인원 추가
        old.getUsers().add(targetUser);
        target.getUsers().add(requester);
    }

    // 사용자 유효성 확인
    @Override
    public AccountRole validateRoleId(Long roleId) {

        return accountRoleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException(roleId + " user is not exist."));
    }

    @Override
    public Store validateStoreId(Long storeId) {

        return storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException(storeId + " store is not exist."));
    }



    @Override
    public DailySchedule validateSchedule(Long scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException(scheduleId + " schedule is not exist."));
    }

    public ChangeRequest validateRequest(Long id) {

        return requestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(id + " request is not exist."));
    }

    public AlarmReceiver validateReceive(Long id) {

        return receiverRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(id + " alarm is not exist."));
    }

    /*
    // 근무 시작 전 알림 가져오기
    public List<Integer> getWorkAlarm(AccountRole role) {

        List<WorkAlarm> alarms = workAlarmRepository.findAllByAccountRole(role);
        List<Integer> result = new ArrayList<>();

        for (WorkAlarm times : alarms) {
            result.add(times.getTime());
        }

        if (alarms.size() == 0) {
            throw new IllegalArgumentException("no data");
        }

        return result;
    }

    // 근무 시작 전 알림 수정하기 -> 근무 전 알림 개수 제한 : 검증단계
    public void updateWorkAlarm(List<Integer> term, AccountRole role) {

        List<WorkAlarm> alarms = workAlarmRepository.findAllByAccountRole(role); // 기존 DB에 저장된 값
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
                tmp.setAccountRole(role);
                tmp.setTime(t);
                newData.add(tmp);
            }
        }
        workAlarmRepository.saveAll(newData);
    }*/

    // 알림의 마지막 id 가져오기
    public long getLastId(AccountRole role) {
        return receiverRepository.selectLastId(role.getId())
                .orElseThrow(() -> new IllegalArgumentException("alarm of " + role.getId() + " account table is empty"));
    }

    // 알림 list 가져오기
    @Transactional
    public List<GetAlarm> getAlarmList(AccountRole accountRole, int pageNum) {

        // pageable 생성, 페이지 번호, 한 페이지의 객체 수
        Pageable page = PageRequest.of(pageNum, 10, Sort.by(Sort.Direction.DESC, "date"));

        List<AlarmReceiver> receiverList
                = receiverRepository.findByAccountRole(accountRole, page);
        List<GetAlarm> result = new ArrayList<>();

        log.info(receiverList.size() + "개의 아이템을 받아왔습니다.");

        for (AlarmReceiver ar : receiverList) {

            AccountRole req = null, rec = null;
            // 근무 교환 종류의 알람이면 req와 rec 가져오기
            if (ar.getAlarm().getType() == Alarm.Category.CHANGE) {
                ChangeRequest changeRequest = ar.getAlarm().getChangeRequest();
                req = validateRoleId(changeRequest.getRequester());
                rec = changeRequest.getReceiver() != null ?
                        validateRoleId(changeRequest.getReceiver()) : null;
            }

            GetAlarm alarm = ar.getAlarm().toGetAlarm(ar.getId(), req, rec);
            alarm.setRead(ar.isCheck());

            // 근무 변경이면 스케줄 날짜 넣기
            if (alarm.getType() == Alarm.Category.CHANGE) {
                DailySchedule oldSchedule = validateSchedule(alarm.getRequest().getOld());
                alarm.getRequest().setOldSchedule(oldSchedule.toFormatString());

                if (alarm.getRequest().getTarget() != 0) {
                    DailySchedule targetSchedule = validateSchedule(alarm.getRequest().getTarget());
                    alarm.getRequest().setTargetSchedule(targetSchedule.toFormatString());
                }
            }

            // 알림 목록에 추가
            result.add(alarm);

            ar.setCheck(true);
        }

        return result;
    }

    // 근무 교환 알림 승낙/거절
    @Transactional
    public int changeAccept(boolean res, AlarmReceiver receiver, AccountRole role) throws IllegalArgumentException {

        // changeRequest 가져오기
        ChangeRequest request = receiver.getAlarm().getChangeRequest();

        if (role.getRole() == Role.EMPLOYEE && !role.equals(receiver.getAccountRole())) {
            throw new IllegalArgumentException(role.getId() + " user doesn't have request.");
        }

        if (receiver.getAlarm().getType() != Alarm.Category.CHANGE) {
            throw new IllegalArgumentException(receiver.getId() + " is not change request.");
        }

        // receiver 대상이 근무자인 경우
        if (receiver.getAccountRole().getRole() == Role.EMPLOYEE) {
            request.setResState(res ? ChangeRequest.State.PASS : ChangeRequest.State.FAIL);
        } else { // 점주인 경우
            request.setOwnState(res ? ChangeRequest.State.PASS : ChangeRequest.State.FAIL);
        }

        // 둘 중 한 명이라도 알림을 거부할 경우
        if (request.getOwnState() == ChangeRequest.State.FAIL
                || request.getResState() == ChangeRequest.State.FAIL) {

            // 요청 자체를 거절
            receiver.getAlarm().setState(Alarm.State.DENIAL);
        }

        // 지정 변경 요청에서 점주와 근무자 모두 교환 요청을 승낙한 경우
        if (request.getResState() == ChangeRequest.State.PASS
                && request.getOwnState() == ChangeRequest.State.PASS) {

            // 스케줄 변경
            changeSchedule(request, receiver);

            // 요청 근무자와 대상 근무자에게 요청이 승낙되었음을 알림? or 스케줄 업데이트 알림
            receiver.getAlarm().setState(Alarm.State.ACCEPT);
        }

        // 비지정 변경 요청자 삭제 -> 빈 자리는 알아서 채우라고 팝업
        if (request.getResState() == ChangeRequest.State.INVALID
                && request.getOwnState() == ChangeRequest.State.PASS) {

            // 비지정 변경 요청자를 스케줄에서 삭제
            DailySchedule oldSchedule = validateSchedule(request.getOldSchedule());
            AccountRole requester = validateRoleId(request.getRequester());
            oldSchedule.getUsers().remove(requester);

            // 비지정 요청자를 삭제하고 근무자가 없어진 경우 스케줄 삭제처리
            if (oldSchedule.getUsers().size() == 0) {
                scheduleRepository.delete(oldSchedule);
            }
        }

        // 비지정 요청인 경우 202 반환
        if (request.getResState() == ChangeRequest.State.INVALID) {
            return 202;
        }
        // 지정 요청인 경우 201 반환
        return 201;
    }

    @Transactional
    public void joinAccept(boolean res, AlarmReceiver receiver, AccountRole role) throws IllegalArgumentException {

        if (role.getSalary()) {
            throw new IllegalArgumentException(role.getId() + " user no permissions");
        }

        if (receiver.getAlarm().getType() != Alarm.Category.JOIN) {
            throw new IllegalArgumentException(receiver.getId() + " is not join request.");
        }

        AccountRole target = validateRoleId(receiver.getAlarm().getContentId());
        target.setValid(res);
    }

    // 알림(AlarmReceiver) 삭제하기
    public void deleteReceiver(AlarmReceiver receiver) {

        receiverRepository.delete(receiver);
    }
}
