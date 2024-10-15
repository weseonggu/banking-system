package com.msa.banking.account.infrastructure.batch;

import com.msa.banking.account.domain.model.AccountTransactions;
import com.msa.banking.account.domain.repository.TransactionsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Collections;


@Slf4j
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class TransactionsJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final TransactionsRepository transactionsRepository;

    @Bean
    public RepositoryItemReader<AccountTransactions> transactionReader() {
        return new RepositoryItemReaderBuilder<AccountTransactions>()
                .name("transactionReader")
                .repository(transactionsRepository)  // 거래 데이터를 읽어옴
                .methodName("findAll")  // JPA 메서드를 통해 데이터를 읽음
                .pageSize(10)  // 페이징 크기 설정
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))  // 정렬 기준 설정
                .build();
    }

    @Bean
    public ItemProcessor<AccountTransactions, AccountTransactions> transactionProcessor() {
        return transaction -> {
            // 거래 데이터를 처리하는 로직


            return null;
        };
    }

    @Bean
    public RepositoryItemWriter<AccountTransactions> transactionWriter() {
        return new RepositoryItemWriterBuilder<AccountTransactions>()
                .repository(transactionsRepository)  // 처리된 결과를 저장할 때 동일한 Transaction 엔티티 사용
                .methodName("save")
                .build();
    }

    @Bean
    public Job balanceVerificationJob(Step step1) {
        return new JobBuilder("balanceVerificationJob", jobRepository)
                .start(step1)
                .build();
    }

    @Bean
    public Step step1(Tasklet tasklet) {
        return new StepBuilder("step1", jobRepository)
                .tasklet(tasklet, transactionManager)
                .build();
    }

    @Bean
    public Tasklet task1() {
        return (contribution, chunkContext) -> {
            // 작업 처리 로직
            System.out.println("Tasklet is running...");
            return RepeatStatus.FINISHED;
        };
    }
}
