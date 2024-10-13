package com.msa.banking.account.infrastructure.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class JobScheduler {

    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry; // 배치 작업이 동적으로 로드되거나 배치 작업을 여러 개 관리해야 할 때
    @Lazy
    private final Job balanceVerificationJob;

    @Scheduled(cron = "0 0 3 * * ?")  // 매일 새벽 3시에 실행
    public void runBatchJob() {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addString("runDate", LocalDateTime.now().toString())
                    .toJobParameters();

            jobLauncher.run(balanceVerificationJob, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
