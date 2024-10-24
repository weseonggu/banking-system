package com.msa.banking.account.infrastructure.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
public class JobScheduler {

    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry; // 배치 작업이 동적으로 로드되거나 배치 작업을 여러 개 관리해야 할 때

    @Scheduled(cron = "0 0 3 * * ?", zone = "Asia/Seoul")// 매일 새벽 3시에 실행
    public void runBalanceVerificationJob() throws Exception {

        System.out.println("Running Balance Verification Job");

        JobParameters params = new JobParametersBuilder()
                .addString("runDate", LocalDateTime.now().toString())
                .toJobParameters();

        jobLauncher.run(jobRegistry.getJob("balanceVerificationJob"), params);
    }


    @Scheduled(cron = "0 0 14 * * ?", zone = "Asia/Seoul")// 매일 오후 2시에 실행
    public void runDirectDebitJob() throws Exception {

        System.out.println("Running Direct Debit Job");

        JobParameters params = new JobParametersBuilder()
                .addString("runDate", LocalDateTime.now().toString())
                .toJobParameters();

        jobLauncher.run(jobRegistry.getJob("directDebitJob"), params);
    }
}
