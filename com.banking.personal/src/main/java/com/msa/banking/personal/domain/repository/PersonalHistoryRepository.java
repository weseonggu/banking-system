package com.msa.banking.personal.domain.repository;

import com.msa.banking.personal.domain.model.PersonalHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface PersonalHistoryRepository extends PersonalHistoryRepositoryCustom{

    Page<PersonalHistory> findAllByIsDeleteFalse(Pageable pageable);

    Optional<PersonalHistory> findById(Long historyId);

    PersonalHistory save(PersonalHistory personalHistory);
}
