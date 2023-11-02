package com.sidam_backend.repo;

import com.sidam_backend.data.ChangeRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ChangeRequestRepository extends CrudRepository<ChangeRequest, Long> {

    List<ChangeRequest> findAllByOldScheduleOrTargetSchedule(Long old, Long target);
}
