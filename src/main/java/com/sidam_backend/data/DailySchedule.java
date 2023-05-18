package com.sidam_backend.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;

@Data
@Entity
@Table(name="daily_schedule")
public class DailySchedule implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private LocalDate date;

    @ElementCollection
    private ArrayList<Boolean> time = new ArrayList<>();

    @OneToOne
    private Store store;

    @ManyToMany
    private ArrayList<UserRole> users;
}
