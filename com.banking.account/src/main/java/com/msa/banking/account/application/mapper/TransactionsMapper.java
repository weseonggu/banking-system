package com.msa.banking.account.application.mapper;

import com.msa.banking.account.domain.model.AccountTransactions;
import com.msa.banking.account.presentation.dto.transactions.BeneficiaryTransactionResponseDto;
import com.msa.banking.account.presentation.dto.transactions.SenderTransactionResponseDto;
import com.msa.banking.common.account.dto.SingleTransactionResponseDto;
import com.msa.banking.account.presentation.dto.transactions.TransactionsListResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface TransactionsMapper {

    @Mapping(source = "account.accountId", target = "accountId")
    SingleTransactionResponseDto toDto(AccountTransactions transaction);

    List<TransactionsListResponseDto> toListDtos(List<AccountTransactions> transactions);

    @Mapping(source = "account.accountId", target = "accountId")
    BeneficiaryTransactionResponseDto toBeneficiaryDto(AccountTransactions transaction);

    @Mapping(source = "account.accountId", target = "accountId")
    SenderTransactionResponseDto toSenderDto(AccountTransactions transaction);
}
