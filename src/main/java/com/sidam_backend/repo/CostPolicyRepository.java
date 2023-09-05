package com.sidam_backend.repo;


import com.sidam_backend.data.CostPolicy;
import com.sidam_backend.data.Store;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CostPolicyRepository extends CrudRepository<CostPolicy, Long> {

    Optional<List<CostPolicy>> findByStore(Store storeId);
}
