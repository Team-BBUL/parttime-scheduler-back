package com.sidam_backend.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name="work_alarm")
public class WorkAlarm implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long alarmId;

    @NotNull
    private long time;
    // 분 단위

    @ManyToOne
    private UserRole userRoleId;
}
