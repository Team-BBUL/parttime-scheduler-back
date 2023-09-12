package com.sidam_backend.repo;

import com.sidam_backend.data.AccountRole;
import com.sidam_backend.data.Incentive;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IncentiveRepository extends CrudRepository<Incentive, Long> {

    Optional<List<Incentive>> findIncentivesByAccountRoleIdOrderByDate(Long employeeId);


    Optional<List<Incentive>> findByAccountRoleAndDateBetween(
            AccountRole accountRole, LocalDate starDate, LocalDate endDate);

}
