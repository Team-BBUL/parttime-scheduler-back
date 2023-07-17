package com.sidam_backend.repo;

import com.sidam_backend.data.Store;
import com.sidam_backend.data.UserRole;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRoleRepository extends CrudRepository<UserRole, Long>{

    Optional<List<UserRole>> findByStore(Store storeId);

    Optional<UserRole> findByIdAndStore(Long id, Store store);

    // 점주 찾기 메소드
    @Query(value = "SELECT * FROM user_role WHERE store_id = :store AND is_salary = false", nativeQuery = true)
    Optional<UserRole> findOwner(Long store);

    // 직원 찾기 메소드
    @Query(value = "SELECT * FROM user_role WHERE store_id = :store AND is_salary = true", nativeQuery = true)
    List<UserRole> findEmployees(Long store);
}
