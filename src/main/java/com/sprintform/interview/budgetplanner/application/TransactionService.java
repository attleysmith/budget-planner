package com.sprintform.interview.budgetplanner.application;

import com.sprintform.interview.budgetplanner.application.mappers.TransactionMapper;
import com.sprintform.interview.budgetplanner.application.model.TransactionInput;
import com.sprintform.interview.budgetplanner.domain.model.enums.Category;
import com.sprintform.interview.budgetplanner.domain.model.entites.Transaction;
import com.sprintform.interview.budgetplanner.domain.services.ReferenceGenerator;
import com.sprintform.interview.budgetplanner.infrastructure.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionMapper transactionMapper;
    @Autowired
    private ReferenceGenerator referenceGenerator;
    @Autowired
    private TransactionRepository transactionRepository;

    @Transactional
    public Transaction createTransaction(TransactionInput transactionInput) {
        Transaction transaction = transactionMapper.toTransaction(transactionInput);
        transaction.setId(referenceGenerator.generate());

        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction updateTransaction(String id, TransactionInput transactionInput) {
        Transaction transaction = getTransaction(id);
        transactionMapper.updateTransaction(transaction, transactionInput);
        return transactionRepository.save(transaction);
    }

    @Transactional
    public void removeTransaction(String id) {
        Transaction transaction = getTransaction(id);
        transactionRepository.delete(transaction);
    }

    @Transactional(readOnly = true)
    public Transaction getTransaction(String id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction does not exist!"));
    }

    @Transactional(readOnly = true)
    public List<Transaction> listTransactions(@Nullable Category category, @Nullable LocalDate startDate, @Nullable LocalDate endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date is after end date!");
        }
        return transactionRepository.findByParams(category, startDate, endDate);
    }
}
