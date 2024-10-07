package com.msa.banking.account.domain.repository;

import com.msa.banking.account.presentation.dto.transactions.TransactionsListResponseDto;
import com.msa.banking.account.presentation.dto.transactions.TransactionsSearchRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransactionsRepositoryCustom {

    Page<TransactionsListResponseDto> searchTransactions(TransactionsSearchRequestDto search, Pageable pageable);
}
