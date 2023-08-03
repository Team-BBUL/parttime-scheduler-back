package com.sidam_backend.repo;

import com.sidam_backend.data.AlarmReceiver;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface AlarmReceiverRepository extends CrudRepository<AlarmReceiver, Long> {

    @Query(value = "SELECT * FROM alarm_receiver WHERE role_id = :role AND id > :last LIMIT :cnt", nativeQuery = true)
    List<AlarmReceiver> findAllByRole(Long role, int cnt, long last);

    @Query(value="SELECT id FROM alarm_receiver WHERE role_id = :id ORDER BY id DESC LIMIT 1", nativeQuery = true)
    Optional<Long> selectLastId(Long id);
}
