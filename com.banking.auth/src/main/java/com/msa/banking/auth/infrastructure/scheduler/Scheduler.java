package com.msa.banking.auth.infrastructure.scheduler;

import com.msa.banking.auth.infrastructure.repository.BlackListTokenRepository;
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

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class Scheduler {

    private final EmployeeRepository employeeRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final BlackListTokenRepository blackListTokenRepository;

    /**
     * 매출성과 | 매월 1일 마스터 슬랙 ID 리스트 product 전송
     */
    @Scheduled(cron = "0 0 0 1 * ?")
    public void findAllMaster() {
        log.info("상품 가입건수 스케줄러 | MASTER slackId 리스트 조회 시도 중 | product 메세지 전송 시도 중");
        
        // 모든 MASTER slackId 리스트 조회
        List<String> slackIds = employeeRepository.findByRole(UserRole.MASTER);
        log.info("마스터 슬랙 ID 리스트: {}", slackIds);
        
        // auth > product 카프카 메세지 전송
        kafkaTemplate.send(Topic.PERFORMANCE_MASTER_SLACK_LIST.getTopic(), EventSerializer.serialize(slackIds));
        log.info("상품 가입건수 스케줄러 | MASTER slackId 리스트 조회 완료 | product 메시지 전송 완료");
    }

    /**
     * 매일 오전 00:00 분 블랙리스트 만료기간 지난 토큰 삭제
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanUpExpiredToken() {
        LocalDateTime now = LocalDateTime.now();

        // 만료된 토큰 삭제 로직을 추가 (예시로 만료 시간이 현재 시간보다 이전인 모든 토큰을 삭제)
        int deletedTokensCount = blackListTokenRepository.deleteByExpirationBefore(now);

        log.info("만료된 JWT 토큰 {}개가 DB에서 삭제되었습니다.", deletedTokensCount);

    }

}
