package com.sprintform.interview.budgetplanner.application;

import com.sprintform.interview.budgetplanner.application.model.BudgetPlan;
import com.sprintform.interview.budgetplanner.application.model.Category;
import com.sprintform.interview.budgetplanner.application.model.Currency;
import com.sprintform.interview.budgetplanner.application.model.Transaction;
import com.sprintform.interview.budgetplanner.infrastructure.repositories.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.sprintform.interview.budgetplanner.application.model.Category.miscellaneous;
import static com.sprintform.interview.budgetplanner.application.model.Category.travel;
import static com.sprintform.interview.budgetplanner.application.model.Currency.EUR;
import static com.sprintform.interview.budgetplanner.application.model.Currency.HUF;
import static java.lang.Math.round;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@SpringBootTest
class BudgetPlannerServiceTest {

    public static final short MIN_HISTORY_DAYS = 20;
    public static final short MAX_HISTORY_DAYS = 365;
    private static final short PREDICTED_DAYS = 30;
    private static final LocalDate TODAY = LocalDate.now();
    private static final LocalDate END_DATE_OF_HISTORY_DATA = TODAY.minusDays(1);

    @Mock
    private TransactionRepository transactionRepository;

    private BudgetPlannerService budgetPlannerService;

    @BeforeEach
    void setUp() {
        budgetPlannerService = new BudgetPlannerService(transactionRepository);
    }

    @ParameterizedTest
    @MethodSource("recentlyStartedSystem")
    @DisplayName("First recorded transaction must be at least 20 days before the base date of the plan")
    void recentlyStartedSystemCannotPredictPlan(LocalDate firstDate, Transaction transaction) {
        given(transactionRepository.getFirstDate()).willReturn(Optional.of(firstDate));
        given(transactionRepository.findByParams(null, firstDate, END_DATE_OF_HISTORY_DATA))
                .willReturn(List.of(transaction));

        // expect
        assertThrows(UnsupportedOperationException.class, () -> budgetPlannerService.plan());
    }

    private static Stream<Arguments> recentlyStartedSystem() {
        return IntStream.range(0, MIN_HISTORY_DAYS).mapToObj(days -> {
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
        int historyRange = MIN_HISTORY_DAYS;
        float rate = (float) PREDICTED_DAYS / historyRange;
        LocalDate firstDate = TODAY.minusDays(historyRange);
        // and
        given(transactionRepository.getFirstDate()).willReturn(Optional.of(firstDate));
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
        given(transactionRepository.findByParams(null, firstDate, END_DATE_OF_HISTORY_DATA))
                .willReturn(List.of(transaction));

        // when
        BudgetPlan plan = budgetPlannerService.plan();

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
    @DisplayName("Only transactions in the last 365 days are considered when calculating a plan")
    void maximumAYearIsConsideredIntoAPlan() {
        // given
        int historyRange = MAX_HISTORY_DAYS + 1;
        LocalDate firstDate = TODAY.minusDays(historyRange);
        // and
        given(transactionRepository.getFirstDate()).willReturn(Optional.of(firstDate));

        // when
        budgetPlannerService.plan();

        // expect
        then(transactionRepository).should(times(0))
                .findByParams(null, firstDate, END_DATE_OF_HISTORY_DATA);
        then(transactionRepository).should(times(1))
                .findByParams(null, TODAY.minusDays(MAX_HISTORY_DAYS), END_DATE_OF_HISTORY_DATA);
    }

    @Test
    void planDifferentiatesCategoriesAndCurrencies() {
        // given
        int historyRange = 45;
        float rate = (float) PREDICTED_DAYS / historyRange;
        LocalDate firstDate = TODAY.minusDays(historyRange);
        //and
        given(transactionRepository.getFirstDate()).willReturn(Optional.of(firstDate));
        // and
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
        given(transactionRepository.findByParams(null, firstDate, END_DATE_OF_HISTORY_DATA))
                .willReturn(List.of(miscTransaction, travelTransaction1, travelTransaction2));

        // when
        BudgetPlan plan = budgetPlannerService.plan();

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
        given(transactionRepository.getFirstDate()).willReturn(Optional.of(firstDate));
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
        given(transactionRepository.findByParams(null, firstDate, END_DATE_OF_HISTORY_DATA))
                .willReturn(List.of(miscTransaction1, miscTransaction2, travelTransaction1, travelTransaction2, travelTransaction3));

        // when
        BudgetPlan plan = budgetPlannerService.plan();

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
}