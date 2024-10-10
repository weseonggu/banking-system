package com.msa.banking.common.account.type;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum AccountType {
    @JsonProperty("CHECKING")  CHECKING, // 입출금 계좌
    @JsonProperty("SAVINGS") SAVINGS,  // 예금 계좌
    @JsonProperty("LOAN") LOAN      // 대출 계좌
}
