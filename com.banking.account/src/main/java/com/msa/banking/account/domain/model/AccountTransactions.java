package com.msa.banking.account.domain.model;

import com.msa.banking.account.infrastructure.encryption.EncryptAttributeConverter;
import com.msa.banking.account.presentation.dto.TransactionsRequestDto;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;


@Getter
@Entity
@Table(name = "p_account_transactions")
@AllArgsConstructor
@NoArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class AccountTransactions {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)   // Long타입에 맞는 난수 발생을 위해 custom함
    private Long transactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(nullable = false)
    private TransactionType type;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    @Builder.Default
    private TransactionStatus status = TransactionStatus.PENDING;

    private String description;

    @Convert(converter = EncryptAttributeConverter.class)
    private String originatingAccount;

    @Convert(converter = EncryptAttributeConverter.class)
    private String beneficiaryAccount;


    //TODO: 계좌 거래 내역같은거는 어떤 특정 권한을 가진 주체가 생성하는 것이 아닌 시스템 상에서 생성하는 것인데 어떻게 해야하나?
    public static AccountTransactions createTransaction(Account account, TransactionsRequestDto requestDto) {

        return AccountTransactions.builder()
                .account(account)
                .type(requestDto.type())
                .amount(requestDto.amount())
                .description(requestDto.description())
                .originatingAccount(requestDto.originatingAccount())
                .beneficiaryAccount(requestDto.beneficiaryAccount())
                .build();
    }

    // 거래 상태와, 거래 설명만 수정 가능
    public void updateTransaction(TransactionStatus status, String description) {
        this.status = status;
        this.description = description;
    }
}
