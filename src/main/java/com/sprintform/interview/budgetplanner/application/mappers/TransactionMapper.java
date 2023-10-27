package com.sprintform.interview.budgetplanner.application.mappers;

import com.sprintform.interview.budgetplanner.application.model.TransactionInput;
import com.sprintform.interview.budgetplanner.domain.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public abstract class TransactionMapper {

    public abstract Transaction toTransaction(TransactionInput transactionInput);

    public abstract void updateTransaction(@MappingTarget Transaction transaction, TransactionInput transactionInput);
}
