package com.sprintform.interview.budgetplanner.application.model;

import com.sprintform.interview.budgetplanner.domain.model.Category;
import com.sprintform.interview.budgetplanner.domain.model.Currency;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

@Builder
@Data
public class BudgetPlan {

    private LocalDate startDate;
    private LocalDate endDate;
    private Map<Category, Map<Currency, Integer>> plan;
}
