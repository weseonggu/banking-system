package com.msa.banking.account.application.mapper;

import com.msa.banking.account.domain.model.DirectDebit;
import com.msa.banking.account.presentation.dto.directDebit.DirectDebitListResponseDto;
import com.msa.banking.account.presentation.dto.directDebit.DirectDebitResponseDto;
import org.mapstruct.Mapper;

import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface DirectDebitMapper {

    DirectDebitResponseDto toDto(DirectDebit directDebit);

    List<DirectDebitListResponseDto> toListDtos(List<DirectDebit> directDebits);
}
