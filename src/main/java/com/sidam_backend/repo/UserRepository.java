package com.sidam_backend.repo;

import com.sidam_backend.data.Account;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<Account, String> {
}
