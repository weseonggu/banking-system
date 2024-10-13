package com.msa.banking.auth.infrastructure.repository;

import com.msa.banking.auth.domain.model.SlackCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SlackCodeRepository extends JpaRepository<SlackCode, Long> {
    void deleteBySlackId(String slackId);

    Optional<SlackCode> findBySlackId(String slackId);
}
