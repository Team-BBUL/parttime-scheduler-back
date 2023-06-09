package com.sidam_backend.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;

@Data
@Entity
@Table(name="daily_schdule")
public class DailySchedule implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long dailyScheduleId;

    @NotBlank
    private String date;
    // yyyy-MM-dd

    @NotBlank
    private String startTime;
    // hh:mm

    @NotBlank
    private String endTime;
    // hh:mm

    @OneToOne
    private Store storeId;

    // 관계형으로 할 때도 list를 썼는데 여기서도 그렇게 해도 되나...?
    @ManyToMany
    private ArrayList<UserRole> userRoleId = new ArrayList<>();
}
