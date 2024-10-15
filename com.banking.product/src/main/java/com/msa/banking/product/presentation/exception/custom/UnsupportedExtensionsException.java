package com.msa.banking.product.presentation.exception.custom;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_GATEWAY)
public class UnsupportedExtensionsException extends RuntimeException {

    public UnsupportedExtensionsException(String msg) {
        super(msg);
    }
}
