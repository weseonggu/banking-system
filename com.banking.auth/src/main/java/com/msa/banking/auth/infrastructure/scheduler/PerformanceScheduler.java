package com.msa.banking.auth.infrastructure.scheduler;

import com.msa.banking.auth.infrastructure.repository.EmployeeRepository;
import com.msa.banking.common.base.UserRole;
import com.msa.banking.common.event.EventSerializer;
import com.msa.banking.common.event.Topic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PerformanceScheduler {

    private final EmployeeRepository employeeRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * 매월 1일 00:00 분 상품 가입건수 MASTER 슬랙 전송 스케줄러
     */
    @Scheduled(cron = "0 0 0 1 * ?")
    public void findAllMaster() {
        log.info("상품 가입건수 스케줄러 | MASTER slackId 리스트 조회 시도 중 | product 메세지 전송 시도 중");
        
        // 모든 MASTER slackId 리스트 조회
        List<String> slackIds = employeeRepository.findByRole(UserRole.MANAGER);
        
        // auth > product 카프카 메세지 전송
        kafkaTemplate.send(Topic.PERFORMANCE_MASTER_SLACK_LIST.getTopic(), EventSerializer.serialize(slackIds));
        log.info("상품 가입건수 스케줄러 | MASTER slackId 리스트 조회 완료 | product 메시지 전송 완료");
    }

}
