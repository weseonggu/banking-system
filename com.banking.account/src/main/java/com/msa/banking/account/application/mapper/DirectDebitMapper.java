package com.msa.banking.account.application.mapper;

import com.msa.banking.account.domain.model.DirectDebit;
import com.msa.banking.account.presentation.dto.directDebit.DirectDebitListResponseDto;
import com.msa.banking.account.presentation.dto.directDebit.DirectDebitResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface DirectDebitMapper {

    @Mapping(target = "accountId", source = "account.accountId") // DirectDebitResponseDto 매핑
    DirectDebitResponseDto toDto(DirectDebit directDebit);

    @Mapping(target = "originatingAccount", source = "account.accountNumber") // DirectDebitListResponseDto의 originatingAccount 매핑
    DirectDebitListResponseDto toListDto(DirectDebit directDebit); // 개별 요소에 대한 매핑

    List<DirectDebitListResponseDto> toListDtos(List<DirectDebit> directDebits); // 리스트 매핑
}
