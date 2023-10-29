package com.sprintform.interview.budgetplanner.domain.services;

import com.sprintform.interview.budgetplanner.application.model.BudgetPlan;
import com.sprintform.interview.budgetplanner.domain.model.Transaction;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.lang.Math.round;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.stream.Collectors.*;

@Service
public class Planner {

    private static final short MIN_HISTORY_DAYS = 20;
    private static final short MAX_HISTORY_DAYS = 365;
    private static final short PREDICTED_DAYS = 30;

    /**
     * Predicts the total amount of the turnover expected for the next {@value PREDICTED_DAYS} days per category and per currency.
     * The historical basis of the calculation is at most the last {@value MAX_HISTORY_DAYS} days, if available.
     * @param baseDate The starting date of the predicted period.
     * @param firstDateProvider The provider function of the first 'paid' date of the recorded transactions. Input: default value, if there are no transactions.
     * @param transactionListProvider The provider function of the list of transactions 'paid' between the given dates. Input: start date and end date of the period.
     * @throws UnsupportedOperationException At least {@value MIN_HISTORY_DAYS} days of history must be available.
     * @return The calculated plan. ({@link BudgetPlan})
     */
    public BudgetPlan plan(LocalDate baseDate,
                           Function<LocalDate, LocalDate> firstDateProvider,
                           BiFunction<LocalDate, LocalDate, List<Transaction>> transactionListProvider) {
        short historyRange = getHistoryRange(
                firstDateProvider
                        .apply(baseDate)
                        .until(baseDate, DAYS)
        );
        List<Transaction> transactionList = transactionListProvider
                .apply(baseDate.minusDays(historyRange), baseDate.minusDays(1));
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

    private short getHistoryRange(long existingDaysOfHistory) {
        if (existingDaysOfHistory > MAX_HISTORY_DAYS) {
            return MAX_HISTORY_DAYS;
        } else if (existingDaysOfHistory >= MIN_HISTORY_DAYS) {
            return (short) existingDaysOfHistory;
        } else {
            throw new UnsupportedOperationException("No proper data to create plan!");
        }
    }
}
