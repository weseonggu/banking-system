package com.msa.banking.personal.infrastructure.persistence;

import com.msa.banking.personal.domain.model.PersonalHistory;
import com.msa.banking.personal.domain.repository.PersonalHistoryRepository;
import com.msa.banking.personal.domain.repository.PersonalHistoryRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonalHistoryRepositoryImpl extends JpaRepository<PersonalHistory, Long>, PersonalHistoryRepository, PersonalHistoryRepositoryCustom {
    @Override
    Page<PersonalHistory> findAllByIsDeleteFalse(Pageable pageable);
}
