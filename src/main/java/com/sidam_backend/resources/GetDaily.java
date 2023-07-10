package com.sidam_backend.resources;

import com.sidam_backend.data.DailySchedule;
import com.sidam_backend.data.Store;
import com.sidam_backend.data.UserRole;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class GetDaily {

    private Long id;
    private LocalDate day;
    private List<Boolean> time = new ArrayList<>();
    private List<Worker> workers;

    public DailySchedule toDailySchedule(LocalDateTime ver, Store store, List<UserRole> workers) {

        DailySchedule result = new DailySchedule();

        result.setId(id);
        result.setDate(day);
        result.setTime(time);
        result.setVersion(ver);
        result.setStore(store);
        result.setUsers(workers);

        return result;
    }
}
