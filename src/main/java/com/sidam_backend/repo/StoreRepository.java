package com.sidam_backend.repo;

import com.sidam_backend.data.Store;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface StoreRepository extends CrudRepository<Store, Long> {

    @Query(value = "SELECT s FROM Store s WHERE s.name LIKE %:name%")
    Optional<List<Store>> findAllByName(String name);

    @Query(value = "select s.name from Store s")
    List<String> findNameAll();
}
