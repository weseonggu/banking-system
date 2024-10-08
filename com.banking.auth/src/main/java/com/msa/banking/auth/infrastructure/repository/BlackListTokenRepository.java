package com.msa.banking.auth.infrastructure.repository;

import com.msa.banking.auth.domain.model.BlackListToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface BlackListTokenRepository extends JpaRepository<BlackListToken, Long> {

    int deleteByExpirationBefore(LocalDateTime now);
}
