package com.msa.banking.performance.infrastructure.scheduler;

import com.msa.banking.performance.domain.model.SalesPerformance;
import com.msa.banking.performance.infrastructure.repository.SalesPerformanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Year;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PerformanceScheduler {

    private final SalesPerformanceRepository salesPerformanceRepository;

    /**
     * 매년 1월 1일 1시 30분 작년 매출 성과 기록 종합 스케줄러
     */
    @Scheduled(cron = "0 30 1 1 1 *")
    @Transactional
    public void yearPerformance() {
        log.info("연도별 매출 성과 기록 시도 중");

        // 작년
        Year year = Year.now().minusYears(1);

        // 작년 월 별 매출성과 전체 조회
        List<SalesPerformance> findAllMonth = salesPerformanceRepository.findByYearMonthStartingWith(year.toString());

        // 매출 총 금액
        BigDecimal totalTransactionAmount = BigDecimal.ZERO;
        // 대출 가입 건수
        Long loanCount = 0L;

        for (SalesPerformance salesPerformance : findAllMonth) {
            totalTransactionAmount = totalTransactionAmount.add(salesPerformance.getTotalTransactionAmount());
            loanCount += salesPerformance.getLoanCount();
        }

        // 작년 한 해 총 매출금액 및 가입 건수 DB 저장
        SalesPerformance salesPerformance = SalesPerformance.createSalesPerformance(totalTransactionAmount, loanCount, year.toString());
        salesPerformanceRepository.save(salesPerformance);
        log.info("연도별 매출 성과 기록 완료");
    }
}
