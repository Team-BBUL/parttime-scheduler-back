package com.sidam_backend.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table
public class AlarmReceiver {

    public AlarmReceiver() {}

    public AlarmReceiver(Alarm alarm, AccountRole role) {
        this.alarm = alarm;
        date = alarm.getDate();
        this.accountRole = role;
        check = false;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "alarm_id")
    private Alarm alarm;

    @NotNull
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private AccountRole accountRole;

    @NotNull
    @Column(name = "check_bit")
    private boolean check;
}
