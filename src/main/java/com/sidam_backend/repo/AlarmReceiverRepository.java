package com.sidam_backend.repo;

import com.sidam_backend.data.AccountRole;
import com.sidam_backend.data.AlarmReceiver;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AlarmReceiverRepository extends JpaRepository<AlarmReceiver, Long> {

    List<AlarmReceiver> findByAccountRole(AccountRole role, Pageable pageable);

    @Query(value="SELECT id FROM alarm_receiver WHERE role_id = :id ORDER BY id DESC LIMIT 1", nativeQuery = true)
    Optional<Long> selectLastId(Long id);
}
