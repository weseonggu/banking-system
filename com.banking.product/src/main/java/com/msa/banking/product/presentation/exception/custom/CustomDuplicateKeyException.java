package com.msa.banking.product.presentation.exception.custom;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CustomDuplicateKeyException extends RuntimeException {
    public CustomDuplicateKeyException(String s) {
        super(s);
    }
}
