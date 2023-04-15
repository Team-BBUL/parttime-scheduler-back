package com.sidam_backend.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name="daily_schdule")
public class DailySchedule implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private LocalDateTime date;

    @NotBlank
    private int startTime;
    // hh

    @NotBlank
    private int endTime;
    // hh

    @OneToOne
    private Store store;

    // 관계형으로 할 때도 list를 썼는데 여기서도 그렇게 해도 되나...?
    @OneToOne
    private WorkerList workerList;
}
