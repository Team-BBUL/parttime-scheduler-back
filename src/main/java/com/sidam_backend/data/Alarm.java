package com.sidam_backend.data;

import com.sidam_backend.resources.DTO.GetAlarm;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "alarm_list")
public class Alarm {

    public Alarm() {}
    public Alarm(Category type, String content, State state) {
        this.type = type;
        this.content = content;
        date = LocalDateTime.now().withNano(0);
        this.state = state;
        changeRequest = null;
    }
    public Alarm(ChangeRequest cr) {
        changeRequest = cr;
        date = LocalDateTime.now().withNano(0);
        type = Category.CHANGE;
        state = State.INVALID;
    }

    public enum Category {
        CHANGE, // 근무 변경
        JOIN, // 매장 가입 요청
        NOTICE, // 공지사항
        SCHEDULE // 스케줄
    }

    public enum State {
        NON, // 미응답
        ACCEPT, // 수락
        DENIAL, // 거절
        INVALID, // 유효하지 않음
        ADD, // 생성
        UPDATE, // 수정
        DELETE, // 삭제
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Enumerated
    private Category type;

    private String content;

    @NotNull
    private State state;

    @NotNull
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "change_id")
    private ChangeRequest changeRequest;

    public GetAlarm toGetAlarm() {

        GetAlarm alarm = new GetAlarm();

        alarm.setId(id);
        alarm.setType(type);
        alarm.setDate(date);
        alarm.setContent(content);
        alarm.setRequest(changeRequest.getRequester());

        return alarm;
    }
}
