package com.sprintform.interview.budgetplanner.interfaces;

import com.sprintform.interview.budgetplanner.application.BudgetPlannerService;
import com.sprintform.interview.budgetplanner.application.model.BudgetPlan;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class BudgetPlannerController {

    private final BudgetPlannerService budgetPlannerService;

    /**
     * Predicts the total amount of the turnover expected for the next {@value BudgetPlannerService#PREDICTED_DAYS} days per category and per currency.
     * The historical basis of the calculation is at most the last {@value BudgetPlannerService#MAX_HISTORY_DAYS} days, if available.
     *
     * @return The calculated plan. ({@link BudgetPlan})
     * @throws UnsupportedOperationException At least {@value BudgetPlannerService#MIN_HISTORY_DAYS} days of history must be available.
     */
    @GetMapping("/plan")
    public BudgetPlan plan() throws UnsupportedOperationException {
        return budgetPlannerService.plan();
    }
}
