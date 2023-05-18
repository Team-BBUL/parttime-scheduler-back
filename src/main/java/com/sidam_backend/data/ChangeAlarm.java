package com.sidam_backend.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "change_alarm")
public class ChangeAlarm implements Serializable {

    private enum State {
        PASS, FAIL, NON
    };

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @NotNull
    private ChangeRequest request;

    @NotNull
    private State state;
}
