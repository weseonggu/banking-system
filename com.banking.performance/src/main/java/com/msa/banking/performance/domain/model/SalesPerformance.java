package com.msa.banking.performance.domain.model;

import com.msa.banking.common.base.AuditEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "p_sales_performance")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("is_delete = false")
@Getter
public class SalesPerformance extends AuditEntity {

    @Id
    @UuidGenerator
    @Column(name = "sales_performance_id")
    private UUID id;

    // 총 거래 금액
    @Column(name = "total_transaction_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalTransactionAmount;

    @Column(name = "loan_count", nullable = false)
    private Long loanCount;

    @Column(name = "deposit_count")
    private Long depositCount;

    // 연도별 평가
    @Column(name = "evaluation_year")
    private String evaluationYear;

    // 월별 평가
    @Column(name = "evaluation_month")
    private String evaluationMonth;

    public SalesPerformance(BigDecimal totalTransactionAmount, Long loanCount, String evaluationMonth) {
        this.totalTransactionAmount = totalTransactionAmount;
        this.loanCount = loanCount;

        if (evaluationMonth.contains("-")) {
            this.evaluationMonth = evaluationMonth;
        } else {
            this.evaluationYear = evaluationMonth;
        }

    }

    /**
     * 생성 메서드
     * @param totalTransactionAmount
     * @param loanCount
     * @param evaluationMonth
     * @return
     */
    public static SalesPerformance createSalesPerformance(BigDecimal totalTransactionAmount, Long loanCount, String evaluationMonth) {
        return new SalesPerformance(totalTransactionAmount, loanCount, evaluationMonth);
    }
}
