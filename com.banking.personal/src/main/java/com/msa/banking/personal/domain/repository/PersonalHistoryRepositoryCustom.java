package com.msa.banking.personal.domain.repository;

import com.msa.banking.personal.domain.model.PersonalHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PersonalHistoryRepositoryCustom {
    Page<PersonalHistory> findByUserIdAndCategoryAndStatus(String categoryName, Boolean status, Pageable pageable);
}
