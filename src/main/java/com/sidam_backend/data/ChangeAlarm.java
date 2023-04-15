package com.sidam_backend.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name="change_alarm")
public class ChangeAlarm implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private boolean answer;

    @NotNull
    private LocalDateTime date = LocalDateTime.now();

    @OneToOne
    private ChangeRequest request;
}
