package com.msa.banking.account.application.mapper;

import com.msa.banking.account.domain.model.AccountTransactions;
import com.msa.banking.account.presentation.dto.transactions.TransactionResponseDto;
import com.msa.banking.account.presentation.dto.transactions.TransactionsListResponseDto;
import org.mapstruct.Mapper;

import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface TransactionsMapper {

    TransactionResponseDto toDto(AccountTransactions transaction);

    List<TransactionsListResponseDto> toListDtos(List<AccountTransactions> transactions);
}
