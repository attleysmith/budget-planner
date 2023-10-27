package com.sprintform.interview.budgetplanner.infrastructure;

import com.sprintform.interview.budgetplanner.domain.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    List<Transaction> findByPaidBetween(LocalDate startDate, LocalDate endDate);

    List<Transaction> findByPaidGreaterThanEqual(LocalDate startDate);

    List<Transaction> findByPaidLessThanEqual(LocalDate endDate);
}
