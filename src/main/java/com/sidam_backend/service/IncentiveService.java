package com.sidam_backend.service;

import com.sidam_backend.data.AccountRole;
import com.sidam_backend.data.Incentive;
import com.sidam_backend.data.Store;
import com.sidam_backend.repo.IncentiveRepository;
import com.sidam_backend.resources.DTO.GetIncentive;
import com.sidam_backend.resources.DTO.GetIncentivesRoleInfo;
import com.sidam_backend.resources.DTO.PostIncentive;
import jakarta.transaction.Transactional;
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

    @Transactional
    public List<GetIncentivesRoleInfo> getWithRoleByDate(Store store, LocalDate date){
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
        return postFormatting(employeesIncentive);
    }

    private LocalDate _getPreviousMonth(int dayOfMonth) {
        LocalDate previousMonth = LocalDate.now().minusMonths(1);
        return previousMonth.withDayOfMonth(dayOfMonth);
    }

    @Transactional
    private List<GetIncentivesRoleInfo> postFormatting(List<List<Incentive>> employeesIncentives) {

        List<GetIncentivesRoleInfo> getIncentivesRoleInfos = new ArrayList<>();
        for (List<Incentive> employeesIncentive : employeesIncentives) {
            if (!employeesIncentive.isEmpty()) {
                GetIncentivesRoleInfo info = new GetIncentivesRoleInfo();

                AccountRole accountRole = employeesIncentive.get(0).getAccountRole();
                info.setRoleId(accountRole.getId());
                info.setAlias(accountRole.getAlias());
                for (Incentive incentive : employeesIncentive) {
                    GetIncentive incentiveForm = new GetIncentive();
                    log.info("incentive id = {}",incentive.getId());
                    incentiveForm.setId(incentive.getId());
                    incentiveForm.setCost(incentive.getCost());
                    incentiveForm.setDescription(incentive.getDescription());
                    incentiveForm.setDate(incentive.getDate());
                    info.getIncentives().add(incentiveForm);
                }

                getIncentivesRoleInfos.add(info);
            }
        }
        return getIncentivesRoleInfos;
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
        for (Incentive incentive : incentiveList) {
            GetIncentive incentiveForm = new GetIncentive();
            incentiveForm.setId(incentive.getId());
            incentiveForm.setCost(incentive.getCost());
            incentiveForm.setDescription(incentive.getDescription());
            incentiveForm.setDate(incentive.getDate());
            info.getIncentives().add(incentiveForm);
        }
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
