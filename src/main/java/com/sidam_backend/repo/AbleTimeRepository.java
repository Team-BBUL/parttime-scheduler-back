package com.sidam_backend.repo;

import com.sidam_backend.data.AbleTime;
import com.sidam_backend.data.Store;
import com.sidam_backend.data.AccountRole;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AbleTimeRepository extends CrudRepository<AbleTime, Long> {

    Optional<AbleTime> findByStoreAndAccountRoleAndDate(Store store, AccountRole user, LocalDate date);

    List<AbleTime> findAllByStore(Store store);
}
