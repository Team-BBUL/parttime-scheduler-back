package com.sidam_backend.data;

import com.sidam_backend.resources.DTO.ImpossibleTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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

    @NotNull
    private LocalDate date;

    @ElementCollection
    private List<Boolean> time;

    @ManyToOne
    @JoinColumn(name="store_id")
    private Store store;

    @ManyToOne
    @JoinColumn(name="role_id")
    private AccountRole accountRole;

    public ImpossibleTime toImpossibleTime() {

        ImpossibleTime imTime = new ImpossibleTime();
        imTime.setId(id);
        imTime.setTime(time);
        imTime.setDate(date);

        return imTime;
    }
}
