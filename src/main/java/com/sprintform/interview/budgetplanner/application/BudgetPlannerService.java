package com.sprintform.interview.budgetplanner.application;

import com.sprintform.interview.budgetplanner.application.model.BudgetPlan;
import com.sprintform.interview.budgetplanner.application.model.Transaction;
import com.sprintform.interview.budgetplanner.infrastructure.repositories.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static java.lang.Math.round;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.stream.Collectors.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class BudgetPlannerService {

    private final TransactionRepository transactionRepository;

    public static final short MIN_HISTORY_DAYS = 20;
    public static final short MAX_HISTORY_DAYS = 365;
    public static final short PREDICTED_DAYS = 30;

    @Transactional(readOnly = true)
    public BudgetPlan plan() throws UnsupportedOperationException {
        LocalDate baseDate = LocalDate.now();
        short historyRange = getHistoryRange(baseDate);
        log.info("The historical basis of the calculation is the last {} days.", historyRange);
        List<Transaction> transactionList = transactionRepository.findByParams(null,
                baseDate.minusDays(historyRange), baseDate.minusDays(1));

        return plan(
                baseDate,
                historyRange,
                transactionList
        );
    }

    private BudgetPlan plan(LocalDate baseDate,
                            short historyRange,
                            List<Transaction> transactionList) {
        float rate = (float) PREDICTED_DAYS / historyRange;

        return BudgetPlan.builder()
                .startDate(baseDate)
                .endDate(baseDate.plusDays(PREDICTED_DAYS - 1))
                .details(transactionList.stream().collect(
                        groupingBy(Transaction::getCategory,
                                groupingBy(Transaction::getCurrency,
                                        collectingAndThen(
                                                reducing(0, Transaction::getSum, Integer::sum),
                                                sum -> round(sum * rate))
                                )
                        )
                ))
                .build();
    }

    private short getHistoryRange(LocalDate baseDate) throws UnsupportedOperationException {
        LocalDate firstTransactionDate = transactionRepository.getFirstDate().orElse(baseDate);
        long existingDaysOfHistory = firstTransactionDate.until(baseDate, DAYS);
        if (existingDaysOfHistory > MAX_HISTORY_DAYS) {
            return MAX_HISTORY_DAYS;
        } else if (existingDaysOfHistory >= MIN_HISTORY_DAYS) {
            return (short) existingDaysOfHistory;
        } else {
            throw new UnsupportedOperationException("No proper data to create plan!");
        }
    }
}
