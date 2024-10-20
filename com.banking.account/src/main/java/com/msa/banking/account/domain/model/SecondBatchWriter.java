package com.msa.banking.account.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Entity
@Table(name = "batch_direct_debit")
@AllArgsConstructor
@NoArgsConstructor
public class SecondBatchWriter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID accountId; // 자동 이체 실행할 계좌 아이디

    private String beneficiaryAccount; // 자동 이체 대상 계좌 번호

    @Column(precision = 15, scale = 2)
    private BigDecimal amount; // 자동 이체 금액

    private Integer transferDate;  // 자동 이체 날짜

    private Boolean isProcessingSuccess;  // 자동 이체 성공 여부
}
