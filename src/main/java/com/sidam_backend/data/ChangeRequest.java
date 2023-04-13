package com.sidam_backend.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name="change_request")
public class ChangeRequest implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long requestId;

    @NotBlank
    private String requester;
    // 요청자 user_role_id

    @NotBlank
    private String responser;
    // 요청 받는 사람 user_role_id

    @ManyToOne
    private DailySchedule dailyScheduleId;
}
