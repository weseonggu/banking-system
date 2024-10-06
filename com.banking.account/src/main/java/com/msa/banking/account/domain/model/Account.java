package com.msa.banking.account.domain.model;


import com.msa.banking.account.infrastructure.encryption.EncryptAttributeConverter;
import com.msa.banking.account.presentation.dto.account.AccountRequestDto;
import com.msa.banking.common.base.AuditEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Entity
@Table(name = "p_account")
@AllArgsConstructor
@NoArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class Account extends AuditEntity {

    @Id// @Id는 기본적으로 @Column(updatable = false, nullable = false) 설정 포함
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID accountId;

    // TODO: 알고리즘으로 랜덤 생성
    @Convert(converter = EncryptAttributeConverter.class)    // 중요 데이터 암호화
    @Column(name = "account_number", nullable = false, unique = true)
    @Pattern(regexp = "\\d{3}-\\d{4}-\\d{7}", message = "계좌번호는 xxx-xxxx-xxxxxxx 형식을 따라야 합니다.")
    private String accountNumber;

    @Convert(converter = EncryptAttributeConverter.class)
    @Column(nullable = false)
    private String accountHolder;

    @Column(precision = 15, scale = 2, nullable = false)
    @Builder.Default// precision
    private BigDecimal balance = BigDecimal.valueOf(0.00);

    @Column(nullable = false)
    @Builder.Default
    private AccountStatus status = AccountStatus.ACTIVE;

    @Column(nullable = false)
    private AccountType type;

    public static Account createAccount(String accountNumber, AccountRequestDto requestDto, String username) {

        return Account.builder()
                .accountNumber(accountNumber)
                .accountHolder(requestDto.accountHolder())
                .status(requestDto.status())
                .type(requestDto.type())
                .build();
    }

    // updatedBy는 contextholder에서 처리하거나 requestHeader에서 어노테이션?으로 처리해야한다.
    // 계좌 상태, 잔액만 변경 가능
    public void updateAccount(AccountStatus status, BigDecimal balance){
        this.status = status;
        this.balance = balance;
    }
}
