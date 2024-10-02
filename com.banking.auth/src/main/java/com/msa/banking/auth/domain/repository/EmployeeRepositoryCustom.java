package com.msa.banking.auth.domain.repository;

import com.msa.banking.auth.presentation.request.SearchRequestDto;
import com.msa.banking.auth.presentation.response.AuthResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeRepositoryCustom {
    Page<AuthResponseDto> findPagingAllEmployee(Pageable pageable, SearchRequestDto condition);
}
