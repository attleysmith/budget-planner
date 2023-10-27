package com.sprintform.interview.budgetplanner.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import static jakarta.persistence.EnumType.STRING;

@Entity
@Builder
@Data
@NoArgsConstructor // JPA needs it
@AllArgsConstructor // Builder needs it
public class Transaction {

    @Id
    @Column(name = "id")
    private String id; // TODO: question - A mintában long típus folytatólagos sorszámozással
    @Column(name = "summary")
    private String summary;
    @Enumerated(STRING)
    @Column(name = "category")
    private Category category;
    @Column(name = "sum")
    private int sum;
    @Enumerated(STRING)
    @Column(name = "currency")
    private Currency currency;
    @Column(name = "paid", columnDefinition = "DATE")
    private LocalDate paid; // TODO: question - A mintában timestamp, de az nem életszerű
}
