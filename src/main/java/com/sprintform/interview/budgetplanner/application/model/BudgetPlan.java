package com.sprintform.interview.budgetplanner.application.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

@Builder
@Data
public class BudgetPlan {

    private LocalDate startDate;
    private LocalDate endDate;
    private Map<Category, Map<Currency, Integer>> details;
}
