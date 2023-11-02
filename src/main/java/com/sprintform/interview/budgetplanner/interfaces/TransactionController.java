package com.sprintform.interview.budgetplanner.interfaces;

import com.sprintform.interview.budgetplanner.application.TransactionService;
import com.sprintform.interview.budgetplanner.application.model.Category;
import com.sprintform.interview.budgetplanner.application.model.Transaction;
import com.sprintform.interview.budgetplanner.application.model.TransactionInput;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping()
    public Transaction createTransaction(@RequestBody @Validated TransactionInput transactionInput) {
        return transactionService.createTransaction(transactionInput);
    }

    @PutMapping("/{id}")
    public Transaction updateTransaction(@PathVariable String id, @RequestBody @Validated TransactionInput transactionInput) {
        return transactionService.updateTransaction(id, transactionInput);
    }

    @DeleteMapping("/{id}")
    public void removeTransaction(@PathVariable String id) {
        transactionService.removeTransaction(id);
    }

    @GetMapping("/{id}")
    public Transaction getTransaction(@PathVariable String id) {
        return transactionService.getTransaction(id);
    }

    @GetMapping
    public List<Transaction> listTransactions(
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        return transactionService.listTransactions(category, startDate, endDate);
    }
}
