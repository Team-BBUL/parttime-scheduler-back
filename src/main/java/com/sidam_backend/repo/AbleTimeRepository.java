package com.sidam_backend.repo;

import com.sidam_backend.data.AbleTime;
import com.sidam_backend.data.Store;
import com.sidam_backend.data.AccountRole;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface AbleTimeRepository extends CrudRepository<AbleTime, Long> {

    AbleTime findByStoreAndAccountRoleAndDate(Store store, AccountRole user, LocalDate date);

    List<AbleTime> findAllByStore(Store store);
}
