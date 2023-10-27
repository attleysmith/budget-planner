package com.sprintform.interview.budgetplanner.application.model;

import com.sprintform.interview.budgetplanner.domain.model.Category;
import com.sprintform.interview.budgetplanner.domain.model.Currency;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class TransactionInput {

    private String summary;
    private Category category;
    private int sum;
    private Currency currency;
    private LocalDate paid;
}
