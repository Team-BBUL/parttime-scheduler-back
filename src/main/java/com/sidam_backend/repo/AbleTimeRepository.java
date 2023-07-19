package com.sidam_backend.repo;

import com.sidam_backend.data.AbleTime;
import com.sidam_backend.data.Store;
import com.sidam_backend.data.AccountRole;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;

public interface AbleTimeRepository extends CrudRepository<AbleTime, Long> {

    AbleTime findByStoreAndAccountRoleAndDate(Store store, AccountRole user, LocalDate date);
}
