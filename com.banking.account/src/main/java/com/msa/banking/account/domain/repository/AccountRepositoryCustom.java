package com.msa.banking.account.domain.repository;

import com.msa.banking.account.presentation.dto.account.AccountListResponseDto;
import org.springframework.data.domain.Page;

public interface AccountRepositoryCustom {

    Page<AccountListResponseDto> searchAccounts();
}
