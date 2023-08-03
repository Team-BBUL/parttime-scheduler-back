package com.sidam_backend.repo;

import com.sidam_backend.data.AccountRole;
import org.springframework.data.repository.CrudRepository;
import com.sidam_backend.data.WorkAlarm;

import java.util.List;

public interface WorkAlarmRepository extends CrudRepository<WorkAlarm, Long> {

    List<WorkAlarm> findAllByAccountRole(AccountRole role);
}
