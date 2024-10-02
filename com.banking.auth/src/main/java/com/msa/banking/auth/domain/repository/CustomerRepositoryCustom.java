package com.msa.banking.auth.domain.repository;

import com.msa.banking.auth.presentation.request.SearchRequestDto;
import com.msa.banking.auth.presentation.response.AuthResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerRepositoryCustom {
    Page<AuthResponseDto> findPagingAllCustomer(Pageable pageable, SearchRequestDto condition);
}
