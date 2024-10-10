package com.msa.banking.personal.infrastructure.service;

import com.msa.banking.common.base.UserRole;
import com.msa.banking.common.notification.NotiType;
import com.msa.banking.common.notification.NotificationRequestDto;
import com.msa.banking.common.response.ErrorCode;
import com.msa.banking.commonbean.exception.GlobalCustomException;
import com.msa.banking.personal.application.dto.personalHistory.PersonalHistoryListDto;
import com.msa.banking.personal.application.dto.personalHistory.PersonalHistoryRequestDto;
import com.msa.banking.personal.application.dto.personalHistory.PersonalHistoryResponseDto;
import com.msa.banking.personal.application.dto.personalHistory.PersonalHistoryUpdateDto;
import com.msa.banking.personal.application.event.AccountCompletedEventDto;
import com.msa.banking.personal.application.event.EventProducer;
import com.msa.banking.personal.application.service.PersonalHistoryService;
import com.msa.banking.personal.application.service.UserService;
import com.msa.banking.personal.domain.enums.PersonalHistoryStatus;
import com.msa.banking.personal.domain.model.Budget;
import com.msa.banking.personal.domain.model.Category;
import com.msa.banking.personal.domain.model.PersonalHistory;
import com.msa.banking.personal.domain.repository.BudgetRepository;
import com.msa.banking.personal.domain.repository.CategoryRepository;
import com.msa.banking.personal.infrastructure.repository.PersonalHistoryJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class PersonalHistoryServiceImpl implements PersonalHistoryService {

    private final PersonalHistoryJpaRepository personalHistoryRepository;
    private final CategoryRepository categoryRepository;
    private final BudgetRepository budgetRepository;
    private final UserService userService;
    private final EventProducer eventProducer;
    private final CacheManager cacheManager;

    /**
     * 개인 내역 목록 조회
     * 조건: 카테고리 이름, 상태
     */
    @Override
    @Cacheable(cacheNames = "personalHistoryListCache")
    public Page<PersonalHistoryListDto> searchPersonalHistory(String categoryName, PersonalHistoryStatus status, Pageable pageable) {

        Page<PersonalHistory> personalHistoryPage = personalHistoryRepository.findByCategoryAndStatus(categoryName, status, pageable);

        return personalHistoryPage.map(PersonalHistoryListDto::toDTO);
    }

    /**
     * 개인 내역 생성 (카테고리 미분류)
     */
    @Override
    @Transactional
    @CachePut(cacheNames = "personalHistoryCache", key = "#result.historyId")
    public PersonalHistoryResponseDto createPersonalHistory(AccountCompletedEventDto accountCompletedEventDto) {

        // 계좌에서 거래가 일어났을 때 데이터를 받아서 개인 내역에 저장
        PersonalHistory personalHistory = PersonalHistory.createPersonalHistory(accountCompletedEventDto);
        PersonalHistory savePersonalHistory = personalHistoryRepository.save(personalHistory);

        UUID userId = savePersonalHistory.getUserId();
        LocalDateTime transactionDate = savePersonalHistory.getTransactionDate();
        BigDecimal transactionAmount = savePersonalHistory.getAmount();

        // 해당 기간에 속하는 모든 예산 설정을 조회
        List<Budget> budgets = budgetRepository.findAllByUserIdAndPeriod(userId, transactionDate);

        // 예산 설정이 존재하면
        if(!budgets.isEmpty()){
            for (Budget budget : budgets) {
                budget.addTransactionAmount(transactionAmount);
                budgetRepository.save(budget);

                log.info("getSpentAmount: " + budget.getSpentAmount());
                log.info("getTotalBudget: " + budget.getTotalBudget());
                log.info(budget.getSpentAmount().compareTo(budget.getTotalBudget()));

                if (budget.getSpentAmount().compareTo(budget.getTotalBudget()) > 0) {

                    // FeignClient를 이용하여 고객 정보를 조회
                    ResponseEntity<?> responseEntity = userService.findCustomerById(
                            budget.getUserId(),
                            personalHistory.getUserId(),
                            "CUSTOMER"
                    );

                    log.info(responseEntity.getBody());

                    // TODO 카프카로 전송
                    if (responseEntity.getBody() instanceof Map<?, ?> responseBody) {
                        Object dataObject = responseBody.get("data");

                        if (dataObject instanceof Map<?, ?> dataMap) {
                            UUID getUserId = UUID.fromString((String) dataMap.get("id"));
                            String getSlackId = (String) dataMap.get("slackId");

                            log.info("UserId: " + getUserId);
                            log.info("slackId: " + getSlackId);

                            // Notification 객체 생성
                            NotificationRequestDto notificationRequestDto = NotificationRequestDto.builder()
                                    .userId(getUserId)
                                    .slackId(getSlackId)
                                    .role(UserRole.CUSTOMER)
                                    .type(NotiType.BUDGET_OVERRUN)
                                    .message("설정한 예산을 초과했습니다.")
                                    .build();

                            // Kafka로 알림 전송
                            eventProducer.sendBudgetOverRunNotification(notificationRequestDto);
                        } else {
                            log.error("Invalid data format in response body");
                        }
                    }
                }
            }
        }
        return PersonalHistoryResponseDto.toDTO(savePersonalHistory);
    }

    /**
     * 개인 내역 단 건 조회
     */
    @Override
    @Cacheable(cacheNames = "personalHistoryCache", key = "#historyId")
    public PersonalHistoryResponseDto findPersonalHistoryById(Long historyId, UUID userId, String userRole) {

        PersonalHistory personalHistory = personalHistoryRepository.findById(historyId).orElseThrow(
                () -> new GlobalCustomException(ErrorCode.PERSONAL_HISTORY_NOT_FOUND)
        );

        if(userRole.equals("CUSTOMER")){
            checkUserAccess(userId, userRole, personalHistory.getUserId());
        }

        return PersonalHistoryResponseDto.toDTO(personalHistory);
    }

    /**
     * 개인 내역 조회 후 카테고리 업데이트
     */
    @Override
    @Transactional
    @CachePut(cacheNames = "personalHistoryCache", key = "#historyId")
    @CacheEvict(cacheNames = "personalHistoryListCache", allEntries = true)
    public PersonalHistoryResponseDto updatePersonalHistoryCategory(PersonalHistoryUpdateDto personalHistoryUpdateDto, Long historyId, UUID userId, String userRole, String userName) {

        PersonalHistory personalHistory = personalHistoryRepository.findById(historyId).orElseThrow(
                () -> new GlobalCustomException(ErrorCode.PERSONAL_HISTORY_NOT_FOUND)
        );

        if(userRole.equals("CUSTOMER")){
            checkUserAccess(userId, userRole, personalHistory.getUserId());
        }

        // 입력받은 카테고리 이름 조회 후 존재하지 않으면 생성 후 저장 -> 카테고리 업데이트
        Optional<Category> optionalCategory = categoryRepository.findByName(personalHistoryUpdateDto.getCategoryName());

        if (optionalCategory.isPresent()) {
            personalHistory.updateCategory(optionalCategory.get(), userName);
            personalHistoryRepository.save(personalHistory);
        } else {
            Category category = Category.createCategory(personalHistoryUpdateDto.getCategoryName());
            categoryRepository.save(category);
            personalHistory.updateCategory(category, userName);
            personalHistoryRepository.save(personalHistory);
        }

        return PersonalHistoryResponseDto.toDTO(personalHistory);
    }

    /**
     * 개인 내역 삭제(Soft Delete)
     */
    @Override
    @Transactional
    @CacheEvict(cacheNames = "personalHistoryCache", key = "#historyId")
    public void deletePersonHistory(Long historyId, UUID userId, String userRole, String userName) {

        log.info("deletePersonHistory Service-------------");

        PersonalHistory personalHistory = personalHistoryRepository.findById(historyId).orElseThrow(
                () -> new GlobalCustomException(ErrorCode.PERSONAL_HISTORY_NOT_FOUND)
        );

        if(userRole.equals("CUSTOMER")){
            checkUserAccess(userId, userRole, personalHistory.getUserId());
        }

        personalHistory.deletePersonalHistory(userName);
        personalHistoryRepository.save(personalHistory);

        // 전체 조회 캐시 삭제 (리스트 캐시)
        Cache personalHistoryListCache = cacheManager.getCache("personalHistoryListCache");
        if (personalHistoryListCache != null) {
            personalHistoryListCache.clear();
        }
    }

    // 고객일 때, 자신의 개인 내역 검색
    public void checkUserAccess(UUID userId, String userRole, UUID historyUserId) {
        if (userRole.equals("CUSTOMER") && !userId.equals(historyUserId)) {
            throw new GlobalCustomException(ErrorCode.USER_FORBIDDEN);
        }
    }

    /**
     * 개인 내역 생성 (카테고리 미분류)-(카프카 알림 테스트용) TODO Account - Kafka 개발 완료 시 삭제
     */
    @Override
    @Transactional
    @CachePut(cacheNames = "personalHistoryCache", key = "#result.historyId")
    public PersonalHistoryResponseDto createPersonalHistory(PersonalHistoryRequestDto requestDto, UUID userId, String userName) {

        // 계좌에서 거래가 일어났을 때 데이터를 받아서 개인 내역에 저장
        PersonalHistory personalHistory = PersonalHistory.createPersonalHistory(requestDto, userId, userName);
        PersonalHistory savePersonalHistory = personalHistoryRepository.save(personalHistory);

        LocalDateTime transactionDate = savePersonalHistory.getTransactionDate();
        BigDecimal transactionAmount = savePersonalHistory.getAmount();

        // 해당 기간에 속하는 모든 예산 설정을 조회
        List<Budget> budgets = budgetRepository.findAllByUserIdAndPeriod(userId, transactionDate);

        // 예산 설정이 존재하면
        if(!budgets.isEmpty()){
            for (Budget budget : budgets) {
                budget.addTransactionAmount(transactionAmount);
                budgetRepository.save(budget);

                log.info("-----------------------------");
                log.info(budget.getId());

                log.info("getSpentAmount: " + budget.getSpentAmount());
                log.info("getTotalBudget: " + budget.getTotalBudget());
                log.info(budget.getSpentAmount().compareTo(budget.getTotalBudget()));

                if (budget.getSpentAmount().compareTo(budget.getTotalBudget()) > 0) {

                    log.info("findCustomerById before ----------------------------------- ");

                    // FeignClient를 이용하여 고객 정보를 조회
                    ResponseEntity<?> responseEntity = userService.findCustomerById(
                            budget.getUserId(),
                            personalHistory.getUserId(),
                            "CUSTOMER"
                    );

                    log.info(responseEntity.getBody());

                    // TODO 카프카로 전송
                    if (responseEntity.getBody() instanceof Map<?, ?> responseBody) {
                        Object dataObject = responseBody.get("data");

                        if (dataObject instanceof Map<?, ?> dataMap) {
                            UUID getUserId = UUID.fromString((String) dataMap.get("id"));
                            String getSlackId = (String) dataMap.get("slackId");

                            log.info("UserId: " + getUserId);
                            log.info("slackId: " + getSlackId);

                            // Notification 객체 생성
                            NotificationRequestDto notificationRequestDto = NotificationRequestDto.builder()
                                    .userId(getUserId)
                                    .slackId(getSlackId)
                                    .role(UserRole.CUSTOMER)
                                    .type(NotiType.BUDGET_OVERRUN)
                                    .message("설정한 예산을 초과했습니다.")
                                    .build();

                            // Kafka로 알림 전송
                            eventProducer.sendBudgetOverRunNotification(notificationRequestDto);
                        } else {
                            log.error("Invalid data format in response body");
                        }
                    }
                }
            }
        }
        return PersonalHistoryResponseDto.toDTO(savePersonalHistory);
    }
}
