package com.sidam_backend.repo;

import com.sidam_backend.data.Alarm;
import com.sidam_backend.data.AlarmReceiver;
import com.sidam_backend.data.UserRole;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface AlarmReceiverRepository extends CrudRepository<AlarmReceiver, Long> {

    Optional<AlarmReceiver> findByUserRoleAndAlarm(UserRole userRole, Alarm alarm);
}
