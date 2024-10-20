package com.msa.banking.account.infrastructure.batch;

import com.msa.banking.account.application.dto.FirstBatchTransactionsDto;
import com.msa.banking.account.domain.model.Account;
import com.msa.banking.account.domain.model.FirstBatchWriter;
import com.msa.banking.account.domain.repository.AccountRepository;
import com.msa.banking.account.domain.repository.FirstBatchWriterRepository;
import com.msa.banking.account.domain.repository.TransactionsRepository;
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
import java.util.Collections;
import java.util.List;


@Slf4j
@Configuration
public class BalanceVerificationJob {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformManager;
    private final TransactionsRepository transactionsRepository;
    private final AccountRepository accountRepository;
    private final FirstBatchWriterRepository firstBatchWriterRepository;

    // 생성자에 @Qualifier 사용
    public BalanceVerificationJob(
            JobRepository jobRepository,
            @Qualifier("batchTransactionManager") PlatformTransactionManager platformManager,
            TransactionsRepository transactionsRepository,
            AccountRepository accountRepository,
            FirstBatchWriterRepository firstBatchWriterRepository) {
        this.jobRepository = jobRepository;
        this.platformManager = platformManager;
        this.transactionsRepository = transactionsRepository;
        this.accountRepository = accountRepository;
        this.firstBatchWriterRepository = firstBatchWriterRepository;
    }

    /** 모든 계좌 잔액과 각 계좌의 전체 거래 내역 비교
     *  1. 모든 계좌 아이디와 계좌 잔액을 읽어온다.
     *  2. 모든 계좌 거래 내역을 계좌 아이디별로 입금액, 출금액을 읽어온다.
     *  3. 모든 계좌에 대해 특정 계좌 잔액과 그 계좌의 거래 내역 합산을 비교한다.
     */
    @Bean
    public Job balanceVerificationJob(Step step) {
        return new JobBuilder("balanceVerificationJob", jobRepository)
                .start(step)
                .build();
    }

    @Bean
    public Step step() {
        return new StepBuilder("step", jobRepository)
                .<Account, FirstBatchWriter> chunk(10, platformManager)
                .reader(accountReader())
                .processor(accountProcessor())
                .writer(accountWriter())
                .build();
    }

    /** JPA 기반으로 Item을 읽을 때 사용
     *  1. 모든 계좌 아이디와 계좌 잔액을 읽어온다.
     */
    @Bean
    public RepositoryItemReader<Account> accountReader() {
        return new RepositoryItemReaderBuilder<Account>()
                .name("accountReader")
                .repository(accountRepository)  // 거래 데이터를 읽어옴
                .methodName("findAllAccountIdAndBalance")  // JPA 메서드를 통해 데이터를 읽음
                .pageSize(10)  // 페이징 크기 설정
                .sorts(Collections.singletonMap("accountId", Sort.Direction.ASC))  // 정렬 기준 설정
                .build();
    }

    @Bean
    public ItemProcessor<Account, FirstBatchWriter> accountProcessor() {
        return account -> {
            // 거래 데이터를 처리하는 로직
            // 현재 계좌 잔액
            BigDecimal currentBalance = account.getBalance();

            // 해당 계좌 아이디의 모든 거래 내역 불러오기
            List<FirstBatchTransactionsDto> transactions = transactionsRepository.findByAccountId(account.getAccountId());

            // 모든 입금액
            BigDecimal totalDeposit = transactions.stream()
                    .map(FirstBatchTransactionsDto::getDepositAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // 모든 출금액
            BigDecimal totalWithdrawal = transactions.stream()
                    .map(FirstBatchTransactionsDto::getWithdrawalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // 입금액에서 출금액 제외
            BigDecimal calculatedBalance = totalDeposit.subtract(totalWithdrawal);

            // 계좌 잔액과 calculatedBalance 비교
            boolean isBalanceMatching = calculatedBalance.compareTo(currentBalance) == 0;

            // FirstBatchWriter 엔티티로 반환 (id는 자동 생성되므로 제외)
            return new FirstBatchWriter(null, account.getAccountId(), currentBalance, calculatedBalance, isBalanceMatching);
        };
    }

    @Bean
    public RepositoryItemWriter<FirstBatchWriter> accountWriter() {
        return new RepositoryItemWriterBuilder<FirstBatchWriter>()
                .repository(firstBatchWriterRepository)  // 처리된 결과를 저장할 때 동일한 Account 엔티티 사용
                .methodName("save")
                .build();
    }
}
