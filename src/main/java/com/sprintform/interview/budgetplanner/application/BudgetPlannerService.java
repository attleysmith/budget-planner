package com.sprintform.interview.budgetplanner.application;

import com.sprintform.interview.budgetplanner.domain.model.dtos.BudgetPlan;
import com.sprintform.interview.budgetplanner.domain.services.Planner;
import com.sprintform.interview.budgetplanner.infrastructure.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class BudgetPlannerService {

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private Planner planner;

    @Transactional(readOnly = true)
    public BudgetPlan plan() {
        return planner.plan(
                LocalDate.now(),
                (defaultValue) -> transactionRepository.getFirstDate().orElse(defaultValue),
                (startDate, endDate) -> transactionRepository.findByParams(null, startDate, endDate)
        );
    }
}
