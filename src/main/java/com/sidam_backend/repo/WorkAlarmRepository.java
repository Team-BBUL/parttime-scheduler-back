package com.sidam_backend.repo;

import com.sidam_backend.data.UserRole;
import org.springframework.data.repository.CrudRepository;
import com.sidam_backend.data.WorkAlarm;

import java.util.List;

public interface WorkAlarmRepository extends CrudRepository<WorkAlarm, Long> {

    List<WorkAlarm> findAllByUserRole(UserRole role);

    WorkAlarm findByTimeAndUserRole(int time, UserRole role);
}
