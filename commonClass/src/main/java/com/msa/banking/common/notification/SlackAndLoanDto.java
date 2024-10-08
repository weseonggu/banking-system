package com.msa.banking.common.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SlackAndLoanDto {

    // 슬랙 ID 리스트
    private List<String> slackIds = new ArrayList<>();

    // 계좌 ID 리스트
    private List<UUID> accountIds = new ArrayList<>();

    // 거래 건수
    private int loanCount;

}
