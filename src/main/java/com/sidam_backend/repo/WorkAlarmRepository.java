package com.sidam_backend.repo;

import org.springframework.data.repository.CrudRepository;
import com.sidam_backend.data.WorkAlarm;

public interface WorkAlarmRepository extends CrudRepository<WorkAlarm, Long> {
}
