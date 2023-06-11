package com.sidam_backend.repo;

import com.sidam_backend.data.AbleTime;
import com.sidam_backend.data.Store;
import com.sidam_backend.data.UserRole;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;

public interface AbleTimeRepository extends CrudRepository<AbleTime, Long> {

    AbleTime findByStoreAndUserAndDate(Store store, UserRole user, LocalDate date);
}
