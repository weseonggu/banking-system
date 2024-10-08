package com.msa.banking.performance.application.service;

import com.msa.banking.common.notification.SlackIdAndLoanAndAmountDto;
import com.msa.banking.performance.domain.model.SalesPerformance;
import com.msa.banking.performance.infrastructure.repository.SalesPerformanceRepository;
import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class SalesPerformanceService {

    @Value("${slack.botKey}")
    private String slackToken;

    private final SalesPerformanceRepository salesPerformanceRepository;

    @Transactional
    public void performance(SlackIdAndLoanAndAmountDto deserialize) throws SlackApiException, IOException, URISyntaxException {

        SalesPerformance salesPerformance = SalesPerformance.createSalesPerformance(
                deserialize.getTotalAmount(),
                Long.parseLong(String.valueOf(deserialize.getLoanCount())),
                YearMonth.now()
        );

        // DB 저장
        SalesPerformance savedPerformance = salesPerformanceRepository.save(salesPerformance);

        // 슬랙 전송
        String message = savedPerformance.getEvaluationMonth() + " - 총 거래금액 = " + savedPerformance.getTotalTransactionAmount()
                + " | 대출 가입 건수 = " + savedPerformance.getLoanCount();

        sendMessage(deserialize.getSlackIds(), message);
    }

    /**
     * 슬랙 봇으로 DM 보내기
     * @throws IOException
     * @throws SlackApiException
     */
    public void sendMessage(List<String> slackIds, String message) throws IOException, SlackApiException, URISyntaxException {
        log.info("슬랙 메세지 전송 시도 중");
        Slack slack = Slack.getInstance();

        for (String slackId : slackIds) {
            ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                    .token(slackToken)
                    .channel(slackId)
                    .text(message)
                    .build();

            ChatPostMessageResponse response = slack.methods().chatPostMessage(request);
            if (response.isOk()) {
                log.info("Message sent successfully to user: " + slackId);
            } else {
                log.error("Error sending message: " + response.getError());
                throw new RuntimeException(response.getError());
            }
            log.info("슬랙 메세지 전송 완료");
        }

    }
}
