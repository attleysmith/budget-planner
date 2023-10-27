package com.sprintform.interview.budgetplanner.application;

import com.sprintform.interview.budgetplanner.application.model.BudgetPlan;
import com.sprintform.interview.budgetplanner.domain.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.reducing;

@Service
public class BudgetPlannerService {

    private static final short TIME_RANGE = 1; // month

    @Autowired
    private TransactionService transactionService;

    @Transactional(readOnly = true)
    public BudgetPlan plan() { // TODO: filter fields
        LocalDate now = LocalDate.now();
        LocalDate earlier = now.minusMonths(TIME_RANGE).plusDays(1);
        LocalDate later = now.plusMonths(TIME_RANGE);

        List<Transaction> transactionList = transactionService.listTransactions(earlier, now);
        return BudgetPlan.builder()
                .startDate(now.plusDays(1))
                .endDate(later)
                .plan(transactionList.stream().collect(
                        groupingBy(Transaction::getCategory,
                                groupingBy(Transaction::getCurrency,
                                        reducing(0, Transaction::getSum, Integer::sum)))))
                .build();
    }
}
