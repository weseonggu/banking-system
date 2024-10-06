package com.msa.banking.personal.domain.model;

import com.msa.banking.common.base.AuditEntity;
import com.msa.banking.common.response.ErrorCode;
import com.msa.banking.commonbean.exception.GlobalCustomException;
import com.msa.banking.personal.application.dto.budget.BudgetRequestDto;
import com.msa.banking.personal.domain.enums.BudgetPeriod;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_budget")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Budget extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "budget_id", nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "period", nullable = false)
    private BudgetPeriod period;

    @Column(name = "total_budget", nullable = false)
    private BigDecimal totalBudget;

    @Column(name = "spent_amount", nullable = false)
    private BigDecimal spentAmount;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    // 예산 설정 생성
    public static Budget createBudget(BudgetRequestDto budgetRequestDto){
        LocalDateTime startDate = budgetRequestDto.getStartDate();
        LocalDateTime endDate = calculateEndDate(startDate, budgetRequestDto.getPeriod());

        return Budget.builder()
                .userId(budgetRequestDto.getUserId())
                .period(budgetRequestDto.getPeriod())
                .totalBudget(budgetRequestDto.getTotalBudget())
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }

    // 예산 종료일 계산
    private static LocalDateTime calculateEndDate(LocalDateTime startDate, BudgetPeriod period) {
        if(period == BudgetPeriod.WEEKLY){
            return startDate.plusWeeks(1);

        } else if(period == BudgetPeriod.MONTHLY){
            return startDate.plusMonths(1);
        }
        throw new GlobalCustomException(ErrorCode.BUDGET_BAD_REQUEST);
    }

}
