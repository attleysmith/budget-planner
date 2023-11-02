package com.sprintform.interview.budgetplanner.application.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

@Builder
@Data
public class TransactionInput {

    @Length(max = 30, message = "The summary may not be longer than 30 characters")
    private String summary;
    @NotNull(message = "Category may not be null")
    private Category category;
    @Positive(message = "Sum must be a positive value")
    private int sum;
    @NotNull(message = "Currency may not be null")
    private Currency currency;
    @NotNull(message = "Paid date may not be null")
    private LocalDate paid;
}
