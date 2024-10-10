package com.msa.banking.common.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SlackIdAndLoanAndAmountDto {

    // 슬랙 ID 리스트
    private List<String> slackIds = new ArrayList<>();

    // 거래 건수
    private int loanCount;

    // 총 거래 금액
    private BigDecimal totalAmount;
}
