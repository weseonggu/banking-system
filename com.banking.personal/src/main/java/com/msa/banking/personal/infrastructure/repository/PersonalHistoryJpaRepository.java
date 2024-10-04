package com.msa.banking.personal.infrastructure.repository;

import com.msa.banking.personal.domain.model.PersonalHistory;
import com.msa.banking.personal.domain.repository.PersonalHistoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonalHistoryJpaRepository extends JpaRepository<PersonalHistory, Long>, PersonalHistoryRepository {
    @Override
    Page<PersonalHistory> findAllByIsDeleteFalse(Pageable pageable);
}
