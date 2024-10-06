package com.msa.banking.account.domain.model;

import com.msa.banking.account.infrastructure.encryption.EncryptAttributeConverter;
import com.msa.banking.account.presentation.dto.transactions.TransactionsRequestDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
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

    // TODO: 서비스에서 accountId를 통해 보내는 계좌 가져오기.
    @Convert(converter = EncryptAttributeConverter.class)  // 데이터 암호화
    private String originatingAccount;

    @Convert(converter = EncryptAttributeConverter.class) // 타 은행과의 계좌거래를 전제하지 않기 때문에 형식 일정.
    @Pattern(regexp = "\\d{3}-\\d{4}-\\d{7}", message = "계좌번호는 xxx-xxxx-xxxxxxx 형식을 따라야 합니다.")
    private String beneficiaryAccount;


    // 계좌 거래 내역같은거는 어떤 특정 권한을 가진 주체가 생성하는 것이 아닌 시스템 상에서 생성하는 것인데 어떻게 해야하나? -> 거래 주차가 생성하는 것으로
    // 송금인 계좌 거래 내역 생성
    public static AccountTransactions createSenderTransaction(Account account, TransactionsRequestDto requestDto) {

        return AccountTransactions.builder()
                .account(account)
                .type(requestDto.type())
                .amount(requestDto.amount())
                .description(requestDto.description())
                .beneficiaryAccount(requestDto.beneficiaryAccount())
                .build();
    }

    // 수취인 계좌 거래 내역 생성
    public static AccountTransactions createBeneficiaryTransaction(Account account, String originatingAccount, TransactionsRequestDto requestDto) {

        return AccountTransactions.builder()
                .account(account)
                .type(requestDto.type())
                .amount(requestDto.amount())
                .description(requestDto.description())
                .originatingAccount(originatingAccount)
                .build();
    }

    // 거래 상태와, 거래 설명만 수정 가능
    public void updateTransaction(TransactionStatus status, String description) {
        this.status = status;
        this.description = description;
    }
}
