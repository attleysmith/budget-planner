package com.sprintform.interview.budgetplanner.application.mappers;

import com.sprintform.interview.budgetplanner.application.model.TransactionInput;
import com.sprintform.interview.budgetplanner.application.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public abstract class TransactionMapper {

    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID().toString())")
    public abstract Transaction toTransaction(TransactionInput transactionInput);

    public abstract void updateTransaction(@MappingTarget Transaction transaction, TransactionInput transactionInput);
}
