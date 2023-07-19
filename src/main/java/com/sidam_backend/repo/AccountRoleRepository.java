package com.sidam_backend.repo;

import com.sidam_backend.data.Store;
import com.sidam_backend.data.AccountRole;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRoleRepository extends CrudRepository<AccountRole, Long>{

    Optional<List<AccountRole>> findByStore(Store storeId);

    Optional<AccountRole> findByIdAndStore(Long id, Store store);
}
