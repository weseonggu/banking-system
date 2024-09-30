package com.msa.banking.common.response;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
public class SuccessResponse<T> implements Serializable {

    private int resultCode;
    private String resultMessage;
    private T data;

    public SuccessResponse(int resultCode, String resultMessage, T data) {
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
        this.data = data;
    }

}
