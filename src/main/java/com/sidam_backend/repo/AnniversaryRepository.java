package com.sidam_backend.repo;

import com.sidam_backend.data.AccountRole;
import com.sidam_backend.data.Anniversary;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface AnniversaryRepository extends CrudRepository<Anniversary, Long> {

    Optional<List<Anniversary>> findAnniversariesByAccountRole(AccountRole accountRole);
}
