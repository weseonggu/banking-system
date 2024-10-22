package com.msa.banking.account.infrastructure.batch;

import com.msa.banking.account.domain.model.Account;
import com.msa.banking.account.domain.model.DirectDebit;
import com.msa.banking.account.domain.model.SecondBatchWriter;
import com.msa.banking.account.domain.repository.DirectDebitRepository;
import com.msa.banking.account.infrastructure.repository.SecondBatchWriterRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Collections;

@Slf4j
@Configuration
public class SecondBatchJob {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformManager;
    private final DirectDebitRepository directDebitRepository;
    private final SecondBatchWriterRepository secondBatchWriterRepository;

    public SecondBatchJob(JobRepository jobRepository,
                          @Qualifier("batchTransactionManager") PlatformTransactionManager platformManager,
                          @Qualifier("directDebitRepository") DirectDebitRepository directDebitRepository,
                          SecondBatchWriterRepository secondBatchWriterRepository) {
        this.jobRepository = jobRepository;
        this.platformManager = platformManager;
        this.directDebitRepository = directDebitRepository;
        this.secondBatchWriterRepository = secondBatchWriterRepository;
    }

    /** 모든 자동 이체에 대하여 설정한 날짜에 자동 이체를 실행한다.
     *  1. 배치를 실행하는 날짜에 자동 이체가 등록된 내역을 읽어온다.
     *  2. 자동 이체를 하려는 계좌의 잔액을 확인을 이체할 금액과 비교한다.
     *  3. 자동 이체를 해야할 계좌로 보낸다.
     */

    @Bean
    public Job directDebitJob(Step step2) {
        return new JobBuilder("directDebitJob", jobRepository)
                .start(step2)
                .build();
    }

    @Bean
    public Step step2() {
        return new StepBuilder("step2", jobRepository)
                .<DirectDebit, SecondBatchWriter> chunk(10, platformManager)
                .reader(directDebitReader())
                .processor(directDebitProcessor())
                .writer(directDebitWriter())
                .build();
    }

    /** JPA 기반으로 Item을 읽을 때 사용
     *   1. 배치를 실행하는 날짜에 자동 이체가 등록된 내역을 읽어온다.
     */
    @Bean
    public RepositoryItemReader<DirectDebit> directDebitReader() {
        return new RepositoryItemReaderBuilder<DirectDebit>()
                .name("directDebitReader")
                .repository(directDebitRepository)
                .methodName("findByTransferDate")
                .arguments(adjustToLastDayOfMonth(LocalDate.now()))  /// 오늘 날짜 기준으로 숫자 전달
                .pageSize(10)
                .sorts(Collections.singletonMap("accountId", Sort.Direction.ASC))
                .build();
    }

    // 31일인 경우에는 자동으로 30일로 변경하여 처리
    // 매월 마지막 날로 조정 (31일이 없으면 30일 또는 그 달의 마지막 날로 처리)
    private Integer adjustToLastDayOfMonth(LocalDate currentDate) {
        int lastDayOfMonth = currentDate.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth();
        return currentDate.getDayOfMonth() == 31 ? lastDayOfMonth : currentDate.getDayOfMonth();
    }


    @Bean
    public ItemProcessor<DirectDebit, SecondBatchWriter> directDebitProcessor() {
        return directDebit -> {
            Account account = directDebit.getAccount();
            BigDecimal balance = account.getBalance();
            BigDecimal transferAmount = directDebit.getAmount();

            // 잔액이 이체 금액보다 많은지 확인
            if (balance.compareTo(transferAmount) >= 0) {
                // 잔액에서 이체 금액을 차감
                account.updateAccountBalance(balance.subtract(transferAmount));

                // 성공한 이체 기록 생성
                return new SecondBatchWriter(null, account.getAccountId(),
                        directDebit.getBeneficiaryAccount(),
                        transferAmount,
                        directDebit.getTransferDate(),
                        true);
            } else {
                // 잔액 부족으로 실패한 경우
                return new SecondBatchWriter(null, account.getAccountId(),
                        directDebit.getBeneficiaryAccount(),
                        transferAmount,
                        directDebit.getTransferDate(),
                        false);
            }
        };
    }

    @Bean
    public RepositoryItemWriter<SecondBatchWriter> directDebitWriter() {
        return new RepositoryItemWriterBuilder<SecondBatchWriter>()
                .repository(secondBatchWriterRepository)
                .methodName("save")
                .build();
    }
}
