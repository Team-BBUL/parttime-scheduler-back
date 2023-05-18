package com.sidam_backend.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Data
@Entity
@Table(name="able_time")
public class AbleTime {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private LocalDateTime date;

    @ElementCollection
    private ArrayList<Boolean> time = new ArrayList<>();

    @ManyToOne
    private Store store;

    @ManyToOne
    private UserRole user;
}
