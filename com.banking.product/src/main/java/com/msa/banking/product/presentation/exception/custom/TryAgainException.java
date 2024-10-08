package com.msa.banking.product.presentation.exception.custom;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class TryAgainException extends RuntimeException {
    public TryAgainException(String message) {
        super(message);
    }
}
