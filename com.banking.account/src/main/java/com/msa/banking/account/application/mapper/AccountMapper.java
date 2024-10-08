package com.msa.banking.account.application.mapper;

import com.msa.banking.account.domain.model.Account;
import com.msa.banking.account.presentation.dto.account.AccountListResponseDto;
import com.msa.banking.account.presentation.dto.account.AccountResponseDto;
import org.mapstruct.Mapper;

import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface AccountMapper {

    AccountResponseDto toDto(Account account);

    List<AccountListResponseDto> toListDtos(List<Account> accounts);
}
