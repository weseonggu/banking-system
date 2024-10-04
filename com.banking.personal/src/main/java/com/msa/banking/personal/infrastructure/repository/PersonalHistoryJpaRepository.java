package com.msa.banking.personal.infrastructure.repository;

import com.msa.banking.personal.domain.model.PersonalHistory;
import com.msa.banking.personal.domain.repository.PersonalHistoryRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonalHistoryJpaRepository extends JpaRepository<PersonalHistory, Long>, PersonalHistoryRepositoryCustom {

    Page<PersonalHistory> findAllByIsDeleteFalse(Pageable pageable);

    Optional<PersonalHistory> findById(Long historyId);

    PersonalHistory save(PersonalHistory personalHistory);
}
