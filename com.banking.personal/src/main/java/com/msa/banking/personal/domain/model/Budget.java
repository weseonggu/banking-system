package com.msa.banking.personal.domain.model;

import com.msa.banking.common.base.AuditEntity;
import com.msa.banking.common.response.ErrorCode;
import com.msa.banking.commonbean.exception.GlobalCustomException;
import com.msa.banking.personal.application.dto.budget.BudgetRequestDto;
import com.msa.banking.personal.application.dto.budget.BudgetUpdateDto;
import com.msa.banking.personal.domain.enums.BudgetPeriod;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_budget")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@SQLRestriction("is_delete = false")
public class Budget extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "budget_id", nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
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
    public static Budget createBudget(BudgetRequestDto budgetRequestDto,UUID userId, String userName){
        LocalDateTime startDate = budgetRequestDto.getStartDate();
        LocalDateTime endDate = calculateEndDate(startDate, budgetRequestDto.getPeriod());

        Budget budget = Budget.builder()
                .userId(userId)
                .period(budgetRequestDto.getPeriod())
                .totalBudget(budgetRequestDto.getTotalBudget())
                .spentAmount(BigDecimal.ZERO)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        budget.setCreateByUserName(userName);

        return budget;
    }

    // 예산 설정 수정
    public void updateBudget(BudgetUpdateDto budgetUpdateDto, String userName){

        if(budgetUpdateDto.getPeriod() != null){
            this.updateBudgetPeriod(budgetUpdateDto.getPeriod());
        }

        if(budgetUpdateDto.getTotalBudget() != null){
            this.updateTotalBudget(budgetUpdateDto.getTotalBudget());
        }

        this.setUpdateByUserName(userName);
    }

    // 예산 설정 기간 수정 메서드
    public void updateBudgetPeriod(BudgetPeriod period){
        this.period = period;
        this.endDate = calculateEndDate(this.startDate, period);
    }

    // 설정한 총 예산 수정 메서드
    public void updateTotalBudget(BigDecimal totalBudget){
        this.totalBudget = totalBudget;
    }

    // 예산 설정 삭제(Soft Delete)
    public void deleteBudget(String userName){
        this.delete(userName);
    }

    // 예산 종료일 계산
    public static LocalDateTime calculateEndDate(LocalDateTime startDate, BudgetPeriod period) {
        if(period == BudgetPeriod.WEEKLY){
            return startDate.plusWeeks(1);

        } else if(period == BudgetPeriod.MONTHLY){
            return startDate.plusMonths(1);
        }
        throw new GlobalCustomException(ErrorCode.BUDGET_BAD_REQUEST);
    }

    // 총 사용 가격에 거래에서 일어난 비용 + 메서드
    public void addTransactionAmount(BigDecimal transactionAmount) {
        this.spentAmount = this.spentAmount.add(transactionAmount);
    }
}
