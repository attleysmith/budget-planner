package com.sprintform.interview.budgetplanner.interfaces;

import com.sprintform.interview.budgetplanner.application.TransactionService;
import com.sprintform.interview.budgetplanner.application.model.TransactionInput;
import com.sprintform.interview.budgetplanner.domain.model.Category;
import com.sprintform.interview.budgetplanner.domain.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(path = "/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping()
    public Transaction createTransaction(@RequestBody TransactionInput transactionInput) {
        return transactionService.createTransaction(transactionInput);
    }

    @PutMapping("/{id}")
    public Transaction updateTransaction(@PathVariable String id, @RequestBody TransactionInput transactionInput) {
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
