package com.sidam_backend.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name="able_time")
public class AbleTime {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private LocalDate date;

    @ElementCollection
    private List<Boolean> time;

    @ManyToOne
    private Store store;

    @ManyToOne
    private UserRole user;
}
