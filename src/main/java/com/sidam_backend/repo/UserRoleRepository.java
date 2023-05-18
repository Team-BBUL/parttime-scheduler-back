package com.sidam_backend.repo;

import com.sidam_backend.data.Store;
import com.sidam_backend.data.UserRole;
import org.springframework.data.domain.Example;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRoleRepository extends CrudRepository<UserRole, Long>{

    public List<UserRole> findByStore(Store storeId);

    public UserRole findByIdAndStore(Long id, Store store);
}
