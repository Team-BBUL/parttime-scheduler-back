package com.sidam_backend.data;

import com.sidam_backend.resources.DTO.GetChange;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name="change_request")
public class ChangeRequest implements Serializable {

    public enum State {
        PASS, FAIL, NON, INVALID
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private Long requester;
    // 요청자 account_role_id

    private Long receiver;
    // 요청 받는 사람 account_role_id

    @NotNull
    @Enumerated
    private State resState;

    @NotNull
    @Enumerated
    private State ownState;

    @NotNull
    private LocalDateTime date;

    @NotNull
    private Long oldSchedule;

    private Long targetSchedule;

    public GetChange toGetChange(AccountRole requester, AccountRole receiver) {

        return new GetChange(id, requester, receiver,
                resState, ownState, oldSchedule, targetSchedule);
    }
}
