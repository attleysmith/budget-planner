package com.sprintform.interview.budgetplanner.infrastructure.repositories;

import com.sprintform.interview.budgetplanner.application.model.Category;
import com.sprintform.interview.budgetplanner.application.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    @Query("SELECT t FROM Transaction t WHERE " +
            "(:category IS NULL OR t.category = :category) AND " +
            "(:startDate IS NULL OR t.paid >= :startDate) AND " +
            "(:endDate IS NULL OR t.paid <= :endDate)")
    List<Transaction> findByParams(
            @Param("category") Category category,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT min(t.paid) FROM Transaction t")
    Optional<LocalDate> getFirstDate();
}
