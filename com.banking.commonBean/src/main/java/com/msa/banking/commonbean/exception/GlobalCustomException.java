package com.msa.banking.commonbean.exception;

import com.msa.banking.common.response.ErrorCode;
import lombok.Getter;

@Getter
public class GlobalCustomException extends RuntimeException{

    private final ErrorCode errorCode;

    public GlobalCustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
