package com.sidam_backend.repo;

import com.sidam_backend.data.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, String> {
}
