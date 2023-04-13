package com.sidam_backend.repo;

import com.sidam_backend.data.ChangeRequest;
import org.springframework.data.repository.CrudRepository;

public interface ChangeRequestRepository extends CrudRepository<ChangeRequest, Long> {
}
