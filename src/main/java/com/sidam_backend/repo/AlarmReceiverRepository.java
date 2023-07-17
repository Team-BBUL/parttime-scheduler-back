package com.sidam_backend.repo;

import com.sidam_backend.data.AlarmReceiver;
import com.sidam_backend.data.UserRole;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AlarmReceiverRepository extends CrudRepository<AlarmReceiver, Long> {

    List<AlarmReceiver> findAllByUserRole(UserRole userRole);
}
