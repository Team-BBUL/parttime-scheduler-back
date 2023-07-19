package com.sidam_backend.data;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name="join_alarm")
public class JoinAlarm implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDateTime date = LocalDateTime.now();

    @OneToOne
    private Store store;

    @OneToOne
    private AccountRole userRole;
}
