package com.msa.banking.account.domain.repository;

import com.msa.banking.account.presentation.dto.account.AccountListResponseDto;
import com.msa.banking.account.presentation.dto.account.AccountSearchRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccountRepositoryCustom {

    Page<AccountListResponseDto> searchAccounts(AccountSearchRequestDto search, Pageable pageable);
}
