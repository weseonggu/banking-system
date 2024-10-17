package com.msa.banking.auth.infrastructure.repository;

import com.msa.banking.auth.domain.model.SlackCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SlackCodeRepository extends JpaRepository<SlackCode, Long> {

    void deleteBySlackId(String slackId);

    @Modifying
    @Query("DELETE FROM SlackCode sc WHERE sc.slackId = :slackId AND sc.isValid = true ")
    void deleteBySlackIdAndIsValid(String slackId);

    Optional<SlackCode> findBySlackId(String slackId);

    Optional<SlackCode> findBySlackIdAndIsValid(String slackId, boolean valid);

    boolean existsBySlackIdAndIsValid(String slackId, boolean b);
}
