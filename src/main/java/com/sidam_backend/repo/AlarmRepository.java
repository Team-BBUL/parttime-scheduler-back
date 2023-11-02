package com.sidam_backend.repo;

import com.sidam_backend.data.Alarm;
import com.sidam_backend.data.ChangeRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AlarmRepository extends CrudRepository<Alarm, Long> {

    @Query(value = "SELECT * FROM alarm_list WHERE role_id = :role ORDER BY date DESC LIMIT :cnt", nativeQuery = true)
    List<Alarm> findCntByUserRole(Long role, int cnt);

    List<Alarm> findAllByChangeRequest(ChangeRequest changeRequest);
}
