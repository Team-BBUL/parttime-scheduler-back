package com.sidam_backend.repo;

import com.sidam_backend.data.DailySchedule;
import com.sidam_backend.data.Store;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface DailyScheduleRepository extends CrudRepository<DailySchedule, Long> {
    DailySchedule findByDateAndStore(LocalDate date, Store store);

    List<DailySchedule> findAllByDateAndStore(LocalDate date, Store store);

    void deleteAllByDateAndStore(LocalDate date, Store store);


}
