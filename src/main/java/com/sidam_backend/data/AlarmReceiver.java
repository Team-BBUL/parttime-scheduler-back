package com.sidam_backend.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table
public class AlarmReceiver {

    public AlarmReceiver() {}

    public AlarmReceiver(Alarm alarm, UserRole role) {
        this.alarm = alarm;
        this.userRole = role;
        check = false;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "alarm_id")
    private Alarm alarm;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private UserRole userRole;

    @NotNull
    @Column(name = "check_bit")
    private boolean check;
}
