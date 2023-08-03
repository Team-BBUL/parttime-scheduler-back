package com.sidam_backend.repo;

import com.sidam_backend.data.Store;
import org.springframework.data.repository.CrudRepository;

public interface StoreRepository extends CrudRepository<Store, Long> {

    Store findByName(String name);
    boolean existsByName(String name);
}
