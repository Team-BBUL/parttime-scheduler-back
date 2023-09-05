package com.sidam_backend.repo;

import com.sidam_backend.data.Store;
import com.sidam_backend.data.AccountRole;
import com.sidam_backend.data.enums.Role;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRoleRepository extends CrudRepository<AccountRole, Long>{


    Optional<List<AccountRole>> findByStore(Store storeId);

    Optional<AccountRole> findByIdAndStore(Long id, Store store);

    Optional<AccountRole> findByAccountIdAndStore(Long id, Store store);

    Optional<List<AccountRole>> findAccountRolesByAccountIdAndRole(Long account, Role role);
    // 점주 찾기 메소드
    @Query(value = "SELECT * FROM account_role WHERE store_id = :store AND role = 'MANAGER'", nativeQuery = true)
    Optional<AccountRole> findOwner(Long store);

    // 직원 찾기 메소드
    @Query(value = "SELECT * FROM account_role WHERE store_id = :store AND role = 'EMPLOYEE' AND valid = true", nativeQuery = true)
    List<AccountRole> findEmployees(Long store);

    // 특정 레벨 이상의 직원 찾기
    @Query(value = "SELECT * FROM account_role WHERE store_id = :store AND role = 'EMPLOYEE' AND level >= :level - 1 AND valid = true", nativeQuery = true)
    List<AccountRole> findEmployeesOverLevel(Long store, int level);

    @EntityGraph(value = "AccountRole.store")
    AccountRole findWithStoreByAccountIdAndStore(Long accountId, Store store);


    @EntityGraph(value = "AccountRole.account")
    AccountRole findWithAccountByAccountIdAndStore(Long accountId, Store store);
}
