package com.sidam_backend.repo;

import com.sidam_backend.data.Account;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AccountRepository extends CrudRepository<Account, String> {
    Optional<Account> findById(Long id);
    Optional<Account> findByEmail(String email);
    Optional<Account> findByOauth2Id(Long oauth2Id);
    boolean existsByIdAndNameIsNotNull(Long id);


}
