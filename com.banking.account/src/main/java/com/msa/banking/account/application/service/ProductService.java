package com.msa.banking.account.application.service;

import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface ProductService {

    ResponseEntity<?> findByAccountId(UUID accountId, UUID userId, String userRole);
}
