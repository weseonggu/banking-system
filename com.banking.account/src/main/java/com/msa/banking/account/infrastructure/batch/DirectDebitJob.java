package com.msa.banking.account.infrastructure.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
public class DirectDebitJob {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformManager;

    public DirectDebitJob(JobRepository jobRepository,
                          @Qualifier("batchTransactionManager") PlatformTransactionManager platformManager) {
        this.jobRepository = jobRepository;
        this.platformManager = platformManager;
    }

    /** 모든 자동 이체에 대하여 설정되 ㄴ
     *  1. 모든 계좌 아이디와 계좌 잔액을 읽어온다.
     *  2. 모든 계좌 거래 내역을 계좌 아이디별로 입금액, 출금액을 읽어온다.
     *  3. 모든 계좌에 대해 특정 계좌 잔액과 그 계좌의 거래 내역 합산을 비교한다.
     */
}
