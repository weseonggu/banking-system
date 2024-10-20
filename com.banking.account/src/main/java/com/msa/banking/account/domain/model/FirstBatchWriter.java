package com.msa.banking.account.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Entity
@Table(name = "batch_account")
@AllArgsConstructor
@NoArgsConstructor
public class FirstBatchWriter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID accountId;  // 비교한 계좌 ID

    @Column(precision = 15, scale = 2)
    private BigDecimal currentBalance;  // 현재 계좌의 잔액

    @Column(precision = 15, scale = 2)
    private BigDecimal calculatedBalance;  // 계산된 거래 내역에 따른 잔액

    private Boolean isBalanceMatching;  // 잔액이 일치하는지 여부
}
