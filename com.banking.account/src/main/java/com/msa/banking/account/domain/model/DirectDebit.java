package com.msa.banking.account.domain.model;

import com.msa.banking.account.infrastructure.encryption.EncryptAttributeConverter;
import com.msa.banking.account.presentation.dto.directDebit.DirectDebitRequestDto;
import com.msa.banking.common.base.AuditEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Entity
@Table(name = "p_direct_debit")
@AllArgsConstructor
@NoArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class DirectDebit extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID directDebitId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "originating_account", referencedColumnName = "account_number")
    private Account account;

    @Convert(converter = EncryptAttributeConverter.class)
    @Column(nullable = false)
    private String beneficiaryAccount;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private Integer transferDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private DirectDebitStatus status = DirectDebitStatus.ACTIVE;

    public static DirectDebit createDirectDebit(Account account, DirectDebitRequestDto requestDto) {
        return DirectDebit.builder()
                .account(account)
                .beneficiaryAccount(requestDto.getBeneficiaryAccount())
                .amount(requestDto.getAmount())
                .transferDate(requestDto.getTransferDate())
                .build();
    }

    // 이체 금액, 이체 날짜, 상태 변경 가능
    public void updateDirectDebit(DirectDebitRequestDto requestDto) {
        this.amount = requestDto.getAmount();
        this.transferDate = requestDto.getTransferDate();
    }

    // 이체 금액, 이체 날짜, 상태 변경 가능
    public void updateDirectDebitStatus(DirectDebitStatus status) {
        this.status = status;
    }
}
