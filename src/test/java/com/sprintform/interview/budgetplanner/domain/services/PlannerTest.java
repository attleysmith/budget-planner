package com.sprintform.interview.budgetplanner.domain.services;

import com.sprintform.interview.budgetplanner.domain.model.dtos.BudgetPlan;
import com.sprintform.interview.budgetplanner.domain.model.entites.Transaction;
import com.sprintform.interview.budgetplanner.domain.model.enums.Category;
import com.sprintform.interview.budgetplanner.domain.model.enums.Currency;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.sprintform.interview.budgetplanner.domain.model.enums.Category.miscellaneous;
import static com.sprintform.interview.budgetplanner.domain.model.enums.Category.travel;
import static com.sprintform.interview.budgetplanner.domain.model.enums.Currency.EUR;
import static com.sprintform.interview.budgetplanner.domain.model.enums.Currency.HUF;
import static java.lang.Math.round;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class PlannerTest {

    private static final LocalDate TODAY = LocalDate.now();
    private static final short PREDICTED_DAYS = 30;

    private final Planner planner = new Planner();

    @ParameterizedTest
    @MethodSource("recentlyStartedSystem")
    @DisplayName("First recorded transaction must be at least 20 days before the base date of the plan")
    void recentlyStartedSystemCannotPredictPlan(LocalDate firstDate, Transaction transaction) {
        // given
        Function<LocalDate, LocalDate> firstDateProvider = defaultValue -> firstDate;
        BiFunction<LocalDate, LocalDate, List<Transaction>> transactionListProvider = (startDate, endDate) ->
                List.of(transaction);

        // expect
        assertThrows(UnsupportedOperationException.class, () ->
                planner.plan(TODAY, firstDateProvider, transactionListProvider));
    }

    private static Stream<Arguments> recentlyStartedSystem() {
        return IntStream.range(0, 20).mapToObj(days -> {
            LocalDate firstDate = TODAY.minusDays(days);
            Transaction transaction = Transaction.builder()
                    .id(UUID.randomUUID().toString())
                    .category(miscellaneous)
                    .summary("first transaction")
                    .sum(1000)
                    .currency(HUF)
                    .paid(firstDate)
                    .build();
            return arguments(firstDate, transaction);
        });
    }

    @Test
    @DisplayName("It's enough if the first transaction is recorded 20 days before the base date of the plan")
    void minimumSystemAgeResultsAPlan() {
        // given
        int historyRange = 20;
        float rate = (float) PREDICTED_DAYS / historyRange;
        LocalDate firstDate = TODAY.minusDays(historyRange);
        // and
        Transaction transaction = Transaction.builder()
                .id(UUID.randomUUID().toString())
                .category(miscellaneous)
                .summary("first transaction")
                .sum(1000)
                .currency(HUF)
                .paid(firstDate)
                .build();
        // and
        Function<LocalDate, LocalDate> firstDateProvider = defaultValue -> firstDate;
        BiFunction<LocalDate, LocalDate, List<Transaction>> transactionListProvider = (startDate, endDate) ->
                List.of(transaction);

        // when
        BudgetPlan plan = planner.plan(TODAY, firstDateProvider, transactionListProvider);

        // then
        assertEquals(TODAY, plan.getStartDate());
        assertEquals(TODAY.plusDays(PREDICTED_DAYS - 1), plan.getEndDate());
        // and
        Map<Category, Map<Currency, Integer>> details = plan.getDetails();
        assertEquals(1, details.size());
        assertNotNull(details.get(miscellaneous));
        // and
        Map<Currency, Integer> miscDetails = details.get(miscellaneous);
        assertEquals(1, miscDetails.size());
        assertNotNull(miscDetails.get(HUF));
        // and
        assertEquals(round(1000 * rate), miscDetails.get(HUF));
    }

    @Test
    void planDifferentiatesCategoriesAndCurrencies() {
        // given
        int historyRange = 45;
        float rate = (float) PREDICTED_DAYS / historyRange;
        LocalDate firstDate = TODAY.minusDays(historyRange);
        //and
        Transaction miscTransaction = Transaction.builder()
                .id(UUID.randomUUID().toString())
                .category(miscellaneous)
                .summary("misc transaction")
                .sum(1000)
                .currency(HUF)
                .paid(firstDate)
                .build();
        // and
        Transaction travelTransaction1 = Transaction.builder()
                .id(UUID.randomUUID().toString())
                .category(travel)
                .summary("travel transaction 1")
                .sum(12000)
                .currency(HUF)
                .paid(firstDate.plusDays(10))
                .build();
        // and
        Transaction travelTransaction2 = Transaction.builder()
                .id(UUID.randomUUID().toString())
                .category(travel)
                .summary("travel transaction 2")
                .sum(500)
                .currency(EUR)
                .paid(firstDate.plusDays(20))
                .build();
        // and
        Function<LocalDate, LocalDate> firstDateProvider = defaultValue -> firstDate;
        BiFunction<LocalDate, LocalDate, List<Transaction>> transactionListProvider = (startDate, endDate) ->
                List.of(miscTransaction, travelTransaction1, travelTransaction2);

        // when
        BudgetPlan plan = planner.plan(TODAY, firstDateProvider, transactionListProvider);

        // then
        Map<Category, Map<Currency, Integer>> details = plan.getDetails();
        assertEquals(2, details.size());
        assertNotNull(details.get(miscellaneous));
        assertNotNull(details.get(travel));
        // and
        Map<Currency, Integer> miscDetails = details.get(miscellaneous);
        assertEquals(1, miscDetails.size());
        assertNotNull(miscDetails.get(HUF));
        // and
        assertEquals(round(1000 * rate), miscDetails.get(HUF));
        // and
        Map<Currency, Integer> travelDetails = details.get(travel);
        assertEquals(2, travelDetails.size());
        assertNotNull(travelDetails.get(HUF));
        assertNotNull(travelDetails.get(EUR));
        // and
        assertEquals(round(12000 * rate), travelDetails.get(HUF));
        assertEquals(round(500 * rate), travelDetails.get(EUR));
    }

    @Test
    void planSumsCategoriesAndCurrencies() {
        // given
        int historyRange = 45;
        float rate = (float) PREDICTED_DAYS / historyRange;
        LocalDate firstDate = TODAY.minusDays(historyRange);
        //and
        Transaction miscTransaction1 = Transaction.builder()
                .id(UUID.randomUUID().toString())
                .category(miscellaneous)
                .summary("misc transaction 1")
                .sum(1000)
                .currency(HUF)
                .paid(firstDate)
                .build();
        //and
        Transaction miscTransaction2 = Transaction.builder()
                .id(UUID.randomUUID().toString())
                .category(miscellaneous)
                .summary("misc transaction 2")
                .sum(2000)
                .currency(HUF)
                .paid(firstDate.plusDays(5))
                .build();
        // and
        Transaction travelTransaction1 = Transaction.builder()
                .id(UUID.randomUUID().toString())
                .category(travel)
                .summary("travel transaction 1")
                .sum(12000)
                .currency(HUF)
                .paid(firstDate.plusDays(10))
                .build();
        // and
        Transaction travelTransaction2 = Transaction.builder()
                .id(UUID.randomUUID().toString())
                .category(travel)
                .summary("travel transaction 2")
                .sum(500)
                .currency(EUR)
                .paid(firstDate.plusDays(20))
                .build();
        // and
        Transaction travelTransaction3 = Transaction.builder()
                .id(UUID.randomUUID().toString())
                .category(travel)
                .summary("travel transaction 3")
                .sum(400)
                .currency(EUR)
                .paid(firstDate.plusDays(25))
                .build();
        // and
        Function<LocalDate, LocalDate> firstDateProvider = defaultValue -> firstDate;
        BiFunction<LocalDate, LocalDate, List<Transaction>> transactionListProvider = (startDate, endDate) ->
                List.of(miscTransaction1, miscTransaction2, travelTransaction1, travelTransaction2, travelTransaction3);

        // when
        BudgetPlan plan = planner.plan(TODAY, firstDateProvider, transactionListProvider);

        // then
        Map<Category, Map<Currency, Integer>> details = plan.getDetails();
        assertEquals(2, details.size());
        assertNotNull(details.get(miscellaneous));
        assertNotNull(details.get(travel));
        // and
        Map<Currency, Integer> miscDetails = details.get(miscellaneous);
        assertEquals(1, miscDetails.size());
        assertNotNull(miscDetails.get(HUF));
        // and
        assertEquals(round(3000 * rate), miscDetails.get(HUF));
        // and
        Map<Currency, Integer> travelDetails = details.get(travel);
        assertEquals(2, travelDetails.size());
        assertNotNull(travelDetails.get(HUF));
        assertNotNull(travelDetails.get(EUR));
        // and
        assertEquals(round(12000 * rate), travelDetails.get(HUF));
        assertEquals(round(900 * rate), travelDetails.get(EUR));
    }

    @Test
    void baseDateCanBeOtherThanToday() {
        // given
        LocalDate baseDate = TODAY.minusDays(1);
        // and
        int historyRange = 20;
        float rate = (float) PREDICTED_DAYS / historyRange;
        LocalDate firstDate = baseDate.minusDays(historyRange);
        // and
        Transaction transaction = Transaction.builder()
                .id(UUID.randomUUID().toString())
                .category(miscellaneous)
                .summary("first transaction")
                .sum(1000)
                .currency(HUF)
                .paid(firstDate)
                .build();
        // and
        Function<LocalDate, LocalDate> firstDateProvider = defaultValue -> firstDate;
        BiFunction<LocalDate, LocalDate, List<Transaction>> transactionListProvider = (startDate, endDate) ->
                List.of(transaction);

        // when
        BudgetPlan plan = planner.plan(baseDate, firstDateProvider, transactionListProvider);

        // then
        assertEquals(baseDate, plan.getStartDate());
        assertEquals(baseDate.plusDays(PREDICTED_DAYS - 1), plan.getEndDate());
        // and
        Map<Category, Map<Currency, Integer>> details = plan.getDetails();
        assertEquals(1, details.size());
        assertNotNull(details.get(miscellaneous));
        // and
        Map<Currency, Integer> miscDetails = details.get(miscellaneous);
        assertEquals(1, miscDetails.size());
        assertNotNull(miscDetails.get(HUF));
        // and
        assertEquals(round(1000 * rate), miscDetails.get(HUF));
    }
}