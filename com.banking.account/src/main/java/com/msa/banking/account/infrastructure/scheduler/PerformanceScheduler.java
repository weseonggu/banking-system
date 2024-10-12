package com.msa.banking.account.infrastructure.scheduler;

import com.msa.banking.common.account.type.TransactionType;
import com.msa.banking.account.domain.repository.AccountRepository;
import com.msa.banking.account.domain.repository.TransactionsRepository;
import com.msa.banking.common.event.EventSerializer;
import com.msa.banking.common.event.Topic;
import com.msa.banking.common.notification.SlackAndLoanDto;
import com.msa.banking.common.notification.SlackIdAndLoanAndAmountDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PerformanceScheduler {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final AccountRepository accountRepository;
    private final TransactionsRepository transactionsRepository;

    @KafkaListener(topics = "performance-master-slack-list", groupId = "ProductService-group")
    public void listen(String message) {

        SlackAndLoanDto deserialize = EventSerializer.deserialize(message, SlackAndLoanDto.class);

        // 슬랙 ID 리스트
        List<String> slackIds = deserialize.getSlackIds();

        // 대출 가입 건수
        int loanCount = deserialize.getLoanCount();

        // 계좌 ID 리스트
        List<UUID> accountIds = deserialize.getAccountIds();

        // 전월의 시작일
        YearMonth lastMonth = YearMonth.now().minusMonths(1);
        LocalDateTime startDateTime = lastMonth.atDay(1).atStartOfDay();

        // 전월의 마지막일
        LocalDateTime endDateTime = lastMonth.atEndOfMonth().atTime(23, 59, 59);

        // 계좌 리스트 조회
//        List<String> findAccountNumber = accountRepository.findByAccountIdInAndCreatedAtBetween(accountIds, startDateTime, endDateTime);

        // 대출 토탈 거래 금액
        BigDecimal totalAmount = transactionsRepository.findTotalAmount(accountIds, TransactionType.LOAN_REPAYMENT, startDateTime, endDateTime);

        SlackIdAndLoanAndAmountDto request = new SlackIdAndLoanAndAmountDto(slackIds, loanCount, totalAmount);

        kafkaTemplate.send(Topic.PERFORMANCE_MASTER_SLACK_LIST_LOAN_COUNT_TOTAL_AMOUNT.getTopic(), EventSerializer.serialize(request));
    }
}
