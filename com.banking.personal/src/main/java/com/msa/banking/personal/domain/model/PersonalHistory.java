package com.msa.banking.personal.domain.model;

import com.msa.banking.common.base.AuditEntity;
import com.msa.banking.personal.application.dto.personalHistory.PersonalHistoryRequestDto;
import com.msa.banking.personal.application.event.AccountCompletedEventDto;
import com.msa.banking.personal.domain.enums.PersonalHistoryStatus;
import com.msa.banking.personal.domain.enums.PersonalHistoryType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_personal_history")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PersonalHistory extends AuditEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private PersonalHistoryType type;

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PersonalHistoryStatus status;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    @Column(name = "description", nullable = false)
    private String description;

    // 개인 내역 생성
    public static PersonalHistory createPersonalHistory(AccountCompletedEventDto accountCompletedEventDto){
        return PersonalHistory.builder()
                .userId(accountCompletedEventDto.getUserId())
                .type(accountCompletedEventDto.getType())
                .amount(accountCompletedEventDto.getAmount())
                .status(PersonalHistoryStatus.UNCLASSIFIED)
                .transactionDate(accountCompletedEventDto.getTransactionDate())
                .description(accountCompletedEventDto.getDescription())
                .build();
    }

    /**
     * 개인 내역 생성 (카테고리 미분류)-(카프카 알림 테스트용) TODO Account - Kafka 개발 완료 시 삭제
     */
    public static PersonalHistory createPersonalHistory(PersonalHistoryRequestDto requestDto, UUID userId, String userName){
        PersonalHistory personalHistory =  PersonalHistory.builder()
                .userId(userId)
                .type(requestDto.getType())
                .amount(requestDto.getAmount())
                .status(PersonalHistoryStatus.UNCLASSIFIED)
                .transactionDate(requestDto.getTransactionDate())
                .description(requestDto.getDescription())
                .build();

        personalHistory.setCreateByUserName(userName);

        return personalHistory;
    }

    // 카테고리 수정
    public void updateCategory(Category newCategory, String userName){
        this.category = newCategory;
        this.status = PersonalHistoryStatus.CLASSIFIED;
        this.setUpdateByUserName(userName);
    }

    // 개인 내역 삭제(Soft Delete)
    public void deletePersonalHistory(String userName){
        this.delete(userName);
    }
}
