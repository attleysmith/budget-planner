package com.sprintform.interview.budgetplanner.domain.model.dtos;

import com.sprintform.interview.budgetplanner.domain.model.enums.Category;
import com.sprintform.interview.budgetplanner.domain.model.enums.Currency;
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
