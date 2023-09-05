package com.sidam_backend.service;

import com.sidam_backend.data.AccountRole;
import com.sidam_backend.data.Incentive;
import com.sidam_backend.data.Store;
import com.sidam_backend.repo.IncentiveRepository;
import com.sidam_backend.resources.DTO.GetIncentivesRoleInfo;
import com.sidam_backend.resources.DTO.PostIncentive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class IncentiveService {

    private final IncentiveRepository incentiveRepository;

    private final EmployeeService employeeService;
    public List<Incentive> getIncentives(Long employeeId) {
        return incentiveRepository.findIncentivesByAccountRoleIdOrderByDate(employeeId)
                .orElse(Collections.emptyList());
    }

    public Incentive getIncentive(Long incentiveId){
        return incentiveRepository.findById(incentiveId)
                .orElseThrow(() -> new IllegalArgumentException(incentiveId + " incentive is not exist"));
    }

    public List<List<Incentive>> getWithRoleByDate(Store store, LocalDate date){
        List<AccountRole> accountRoles = employeeService.getAllEmployees(store);

        LocalDate previousDate = _getPreviousMonth(store.getPayday());
        List<List<Incentive>> employeesIncentive = new ArrayList<>();
        for (AccountRole accountRole : accountRoles) {
            if(!accountRole.isManager()){
                employeesIncentive.add(incentiveRepository.
                        findByAccountRoleAndDateBetween(accountRole,previousDate,date)
                        .orElse(Collections.emptyList()));
            }
        }
        return employeesIncentive;
    }

    private LocalDate _getPreviousMonth(int dayOfMonth) {
        LocalDate previousMonth = LocalDate.now().minusMonths(1);
        return previousMonth.withDayOfMonth(dayOfMonth);
    }

    public Incentive createNewIncentive(PostIncentive postIncentive, Long employeeId) {
        AccountRole employee = employeeService.validateRoleId(employeeId);
        Incentive incentive = new Incentive();
        incentive.setCost(postIncentive.getCost());
        incentive.setDescription(postIncentive.getDescription());
        incentive.setDate(postIncentive.getDate());
        incentive.setAccountRole(employee);
        return incentiveRepository.save(incentive);
    }

    public Incentive updateIncentive(PostIncentive postIncentive, Long incentiveId){
        Incentive incentive = this.getIncentive(incentiveId);
        incentive.setCost(postIncentive.getCost());
        incentive.setDescription(postIncentive.getDescription());
        incentive.setDate(postIncentive.getDate());
        return incentiveRepository.save(incentive);
    }

    public GetIncentivesRoleInfo getIncentivesByDate(Long employeeId, int payday, String month) {
        LocalDate date = monthToLocalDate(month);
        AccountRole employee = employeeService.validateRoleId(employeeId);

        LocalDate previousDate = _getPreviousMonth(payday);
        List<Incentive> incentiveList = incentiveRepository.
                findByAccountRoleAndDateBetween(employee, previousDate, date)
                .orElse(Collections.emptyList());

        GetIncentivesRoleInfo info = new GetIncentivesRoleInfo();

        if(incentiveList.isEmpty()){
            return info;
        }
        info.setRoleId(employeeId);
        info.setAlias(employee.getAlias());
        info.setIncentives(incentiveList);
        return info;
    }

    public void deleteIncentive(Long incentiveId){
        Incentive incentive = this.getIncentive(incentiveId);
        log.info("incentiveId = {}", incentive.getId());
        incentiveRepository.delete(incentive);
    }

    private LocalDate monthToLocalDate(String month) {
        YearMonth yearMonth = YearMonth.parse(month);
        return LocalDate.from(yearMonth.atDay(1).atStartOfDay());
    }
}
