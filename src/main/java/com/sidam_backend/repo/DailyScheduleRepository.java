package com.sidam_backend.repo;

import com.sidam_backend.data.DailySchedule;
import org.springframework.data.repository.CrudRepository;

public interface DailyScheduleRepository extends CrudRepository<DailySchedule, Long> {
}
