package com.sidam_backend.data;

import com.sidam_backend.resources.DTO.GetDaily;
import com.sidam_backend.resources.DTO.Worker;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    private List<AccountRole> users = new ArrayList<>();

    @NotNull
    private LocalDateTime version;

    public GetDaily toDaily(AccountRole role) {

        GetDaily daily = new GetDaily();

        daily.setId(id);
        daily.setDay(date);
        daily.setTime(time);

        List<Worker> workers = new ArrayList<>();
        for(AccountRole accountRole : users) {
            workers.add(accountRole.toWorker(role));
        }
        daily.setWorkers(workers);

        return daily;
    }

    public String toFormatString() {

        int i = 0, start = -1, end = 0;
        for (boolean t : time) {
            if (t && start == -1) {
                start = i;
            }

            if (start != -1 && !t) {
                end = i;
                break;
            }

            i++;
        }

        return date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.KOREAN)
                + "(" + date.getDayOfMonth() + "일) "
                + (store.getOpen() + start) + ":00-" + (store.getOpen() + end) + ":00";
    }
}
