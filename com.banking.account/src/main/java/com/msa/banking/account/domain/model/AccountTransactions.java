package com.msa.banking.account.domain.model;

import com.msa.banking.account.infrastructure.encryption.EncryptAttributeConverter;
import com.msa.banking.account.presentation.dto.transactions.TransferTransactionRequestDto;
import com.msa.banking.account.presentation.dto.transactions.WithdrawalTransactionRequestDto;
import com.msa.banking.common.account.dto.DepositTransactionRequestDto;
import com.msa.banking.common.account.type.TransactionStatus;
import com.msa.banking.common.account.type.TransactionType;
import com.msa.banking.common.base.AuditEntity;
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
public class AccountTransactions extends AuditEntity {


    // Long타입에 맞는 난수 발생을 위해 custom함 -> 철회
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_seq")
    @SequenceGenerator(name = "transaction_seq", sequenceName = "transaction_sequence", allocationSize = 50)
    private Long transactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(precision = 15, scale = 2)
    private BigDecimal depositAmount; // 입금액(증가액)

    @Column(precision = 15, scale = 2)
    private BigDecimal withdrawalAmount;  // 출금액(감소액)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TransactionStatus status = TransactionStatus.PENDING;

    private String description;

    @Convert(converter = EncryptAttributeConverter.class)  // 데이터 암호화
    private String originatingAccount;

    @Convert(converter = EncryptAttributeConverter.class) // 타 은행과의 계좌거래를 전제하지 않기 때문에 형식 일정.
    @Pattern(regexp = "\\d{3}-\\d{4}-\\d{7}", message = "계좌번호는 xxx-xxxx-xxxxxxx 형식을 따라야 합니다.")
    private String beneficiaryAccount;

    // TODO: 받는 사람 계좌 소유주 이름 추가?

    // 데이터베이스에 저장되기 전에 유효성을 검증
    @PrePersist
    @PreUpdate
    private void validateAmounts() {
        if ((depositAmount == null || depositAmount.compareTo(BigDecimal.ZERO) == 0) &&
                (withdrawalAmount == null || withdrawalAmount.compareTo(BigDecimal.ZERO) == 0)) {
            throw new IllegalArgumentException("입금액과 출금액 중 하나는 반드시 0이 아닌 값이어야 한다.");
        }
    }

    // 계좌 거래 내역같은거는 어떤 특정 권한을 가진 주체가 생성하는 것이 아닌 시스템 상에서 생성하는 것인데 어떻게 해야하나? -> 거래 주체가 생성하는 것으로
    // 단일 계좌 입금 거래 내역 생성
    public static AccountTransactions createSingleDepositTransaction(Account account, DepositTransactionRequestDto requestDto) {

        return AccountTransactions.builder()
                .account(account)
                .type(requestDto.type())
                .depositAmount(requestDto.depositAmount())
                .description(requestDto.description())
                .build();
    }

    // 단일 계좌 입금 거래 내역 생성
    public static AccountTransactions createLoanDepositTransaction(Account account, DepositTransactionRequestDto requestDto) {

        return AccountTransactions.builder()
                .account(account)
                .type(requestDto.type())
                .depositAmount(requestDto.depositAmount())
                .description(requestDto.description())
                .build();
    }

    // 단일 계좌 출금 거래 내역 생성
    public static AccountTransactions createSingleWithdrawalTransaction(Account account, WithdrawalTransactionRequestDto requestDto) {

        return AccountTransactions.builder()
                .account(account)
                .type(requestDto.type())
                .withdrawalAmount(requestDto.withdrawalAmount())// requestDto.amount().negate()) 음수 값을 데이터베이스에 저장하는 것은 지양되기 때문에 철회
                .description(requestDto.description())
                .build();
    }


    // TODO: description이 null이면 수취인의 실명을 넣는다. 서비스 로직으로 처리
    // 송금인 계좌 거래 내역
    public static AccountTransactions createSenderTransaction(Account account, TransferTransactionRequestDto requestDto) {

        return AccountTransactions.builder()
                .account(account)
                .type(requestDto.type())
                .withdrawalAmount(requestDto.amount())
                .description(requestDto.description())
                .beneficiaryAccount(requestDto.beneficiaryAccount())
                .build();
    }

    // TODO: description이 null이면 송금인의 실명을 넣는다. 서비스 로직으로 처리
    // 수취인 계좌 거래 내역 생성
    public static AccountTransactions createBeneficiaryTransaction(Account account, String originatingAccount, TransferTransactionRequestDto requestDto) {

        return AccountTransactions.builder()
                .account(account)
                .type(requestDto.type())
                .depositAmount(requestDto.amount())
                .description(requestDto.description())
                .originatingAccount(originatingAccount)
                .build();
    }

    // 거래 상태 수정
    public void updateTransactionStatus(TransactionStatus status) {
        this.status = status;
    }

    // 거래 설명 수정
    public void updateTransactionDescription(String description) {
        this.description = description;
    }
}