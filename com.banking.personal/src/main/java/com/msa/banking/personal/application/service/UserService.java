package com.msa.banking.personal.application.service;

import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface UserService {

    ResponseEntity<?> findCustomerById(UUID customerId, UUID userId, String userRole);
}
