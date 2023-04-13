package com.sidam_backend.repo;

import com.sidam_backend.data.Anniversary;
import org.springframework.data.repository.CrudRepository;

public interface AnniversaryRepository extends CrudRepository<Anniversary, String> {
}
