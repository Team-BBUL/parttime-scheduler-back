package com.sidam_backend.data;

import com.sidam_backend.resources.PostDaily;
import com.sidam_backend.resources.Worker;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name="daily_schedule")
public class DailySchedule implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private LocalDate date;

    @ElementCollection
    private List<Boolean> time;

    @OneToOne
    private Store store;

    @ManyToMany
    @JoinTable(name="workers")
    private List<UserRole> users = new ArrayList<>();

    @NotNull
    private LocalDateTime version;

    public PostDaily toDaily(UserRole role) {

        PostDaily daily = new PostDaily();

        daily.setId(id);
        daily.setDay(date);
        daily.setTime(time);

        List<Worker> workers = new ArrayList<>();
        for(UserRole userRole : users) {
            workers.add(userRole.toWorker(role));
        }
        daily.setWorkers(workers);

        return daily;
    }
}
