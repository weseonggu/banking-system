package com.msa.banking.product.infrastructure.scheduler;

import com.msa.banking.common.event.EventSerializer;
import com.msa.banking.common.event.Topic;
import com.msa.banking.common.notification.SlackAndLoanDto;
import com.msa.banking.product.domain.model.UsingProduct;
import com.msa.banking.product.infrastructure.repository.UsingProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PerformanceScheduler {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final UsingProductRepository usingProductRepository;

    /**
     * 매출성과 | 대출 가입 전월 초에서 전월 말 전체 조회 후 Account 서비스 전달
     * @param message
     */
    @KafkaListener(topics = "performance-master-slack-list", groupId = "AuthService-group")
    public void listen(String message) {
        log.info("매출성과 | 대출 가입 전월 초에서 전월 말 전체 조회 후 Account 서비스 전달 시도 중");

        // 슬랙 ID 리스트
        List<?> request = EventSerializer.deserialize(message, List.class);
        List<String> slackIds = new ArrayList<>();

        for (Object o : request) {
            slackIds.add(String.valueOf(o));
        }
        for (int i = 0; i < request.size(); i++) {
            System.out.println("master slackId " + i + "= " + slackIds.get(i));
        }

//        // 전월의 시작일
//        YearMonth lastMonth = YearMonth.now().minusMonths(1);
//        LocalDateTime startDateTime = lastMonth.atDay(1).atStartOfDay();
//
//        // 전월의 마지막일
//        LocalDateTime endDateTime = lastMonth.atEndOfMonth().atTime(23, 59, 59);

        // 현재 월의 YearMonth 객체 생성
        YearMonth currentMonth = YearMonth.now();
        // 현재 월의 시작일 (1일 00:00:00)
        LocalDateTime startDateTime = currentMonth.atDay(1).atStartOfDay();

        // 현재 월의 마지막일 (23:59:59)
        LocalDateTime endDateTime = currentMonth.atEndOfMonth().atTime(23, 59, 59);

        // 전 월 첫째일 00:00 ~ 전 월 마지막일 23:59:59
        // 대출 조회
        List<UsingProduct> findUsingProduct = usingProductRepository.findByLoanInUseIsNotNullAndCreatedAtBetween(startDateTime, endDateTime);
        System.out.println("대출 가입 사이즈 = " + findUsingProduct.size());

        // 대출 계좌 ID 리스트
        List<UUID> accountIds = new ArrayList<>();

        for (UsingProduct usingProduct : findUsingProduct) {
            accountIds.add(usingProduct.getAccountId());
        }

        SlackAndLoanDto slackAndLoanDto = new SlackAndLoanDto(slackIds, accountIds, findUsingProduct.size());

        kafkaTemplate.send(Topic.PERFORMANCE_MASTER_SLACK_LIST_LOAN_LIST.getTopic(), EventSerializer.serialize(slackAndLoanDto));
        log.info("매출성과 | 대출 가입 전월 초에서 전월 말 전체 조회 후 Account 서비스 전달 완료");
    }
}
