package com.msa.banking.account.domain.repository;

import com.msa.banking.account.presentation.dto.directDebit.DirectDebitListResponseDto;
import com.msa.banking.account.presentation.dto.directDebit.DirectDebitSearchRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DirectDebitRepositoryCustom {

    Page<DirectDebitListResponseDto> searchDirectDebits(DirectDebitSearchRequestDto search, Pageable pageable);
}
