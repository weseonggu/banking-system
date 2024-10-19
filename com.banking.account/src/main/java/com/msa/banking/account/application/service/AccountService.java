package com.msa.banking.account.application.service;

import com.msa.banking.account.application.mapper.AccountMapper;
import com.msa.banking.account.domain.model.Account;
import com.msa.banking.account.domain.repository.AccountRepository;
import com.msa.banking.account.infrastructure.accountgenerator.AccountNumberGenerator;
import com.msa.banking.account.presentation.dto.account.*;
import com.msa.banking.common.account.dto.AccountRequestDto;
import com.msa.banking.common.account.dto.AccountResponseDto;
import com.msa.banking.common.account.type.AccountStatus;
import com.msa.banking.common.base.UserRole;
import com.msa.banking.common.response.ErrorCode;
import com.msa.banking.commonbean.annotation.LogDataChange;
import com.msa.banking.commonbean.exception.GlobalCustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Log4j2
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final ProductService productService;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String PIN_FAILURE_KEY_PREFIX = "accountPin:failure:";
    private static final long ONE_WEEK_IN_SECONDS = 604800; // 7일을 초로 변환한 값


    /**
     * 계좌 등록
     */
    @LogDataChange
    @Transactional
    public UUID createAccount(AccountRequestDto request, UUID userId, String role) {

        // 계좌 번호를 특정 형식에 맞게 랜덤으로 생성
        String accountNumber = AccountNumberGenerator.generateAccountNumber();

        // 생성된 계좌 번호가 기존의 계좌 번호와 겹치는지 확인
        while (accountRepository.findByAccountNumber(accountNumber).isPresent()) {
            System.out.println(ErrorCode.ACCOUNT_NUMBER_ALREADY_EXISTS);
            accountNumber = AccountNumberGenerator.generateAccountNumber();
        }

        Account account = Account.createAccount(accountNumber, request);
        accountRepository.save(account);

        // 등록된 계좌ID 반환
        return account.getAccountId();
    }


    /**
     * 계좌 상태 변경
     * 마스터, 매니저만 변경 가능하다.
     */
    @LogDataChange
    @Transactional
    public AccountResponseDto updateAccountStatus(UUID accountId, AccountStatus status) {

        Account account = accountRepository.findById(accountId)
                .filter(a -> !a.getIsDelete())
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        account.updateAccountStatus(status);

        // mapstruct 라이브러리를 사용하여 entity -> dto 변환
        return accountMapper.toDto(account);
    }


    /**
     * 계좌 비밀번호 변경
     */
    @LogDataChange
    @Transactional
    public void updateAccountPin(UUID accountId, UpdateAccountPinRequestDto request, UUID userId, String role) {

        // 고객의 경우 본인만 계좌 비밀번호 변경 가능
        if(role.equals(UserRole.CUSTOMER.name()) && !productService.findByAccountId(accountId, userId, role).getStatusCode().is2xxSuccessful()){
            throw new GlobalCustomException(ErrorCode.FORBIDDEN);
        }

        Account account = accountRepository.findById(accountId)
                .filter(a -> !a.getIsDelete() && a.getStatus().equals(AccountStatus.ACTIVE))
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        // 입력한 비밀번호가 계좌 비밀번호와 맞는지 체크
        if(!account.getAccountNumber().equals(request.getOriginAccountPin())) {
            throw new GlobalCustomException(ErrorCode.ACCOUNT_PIN_NOT_MATCH);
        }

        // 변경할 비밀번호와 재확인 비밀번호가 맞는지 체크
        if(!request.getChangeAccountPin().equals(request.getCheckAccountPin())) {
            throw new GlobalCustomException(ErrorCode.CHECK_ACCOUNT_PIN_NOT_MATCH);
        }

        account.updateAccountPin(request.getChangeAccountPin());
    }

    /**
     * 계좌 1회 출금 한도액 재설정
     */
    @LogDataChange
    @Transactional
    public void updatePerWithdrawalLimit(UUID accountId, PerWithdrawalLimitRequestDto request, UUID userId, String role) {

        if(role.equals(UserRole.CUSTOMER.name()) && !productService.findByAccountId(accountId, userId, role).getStatusCode().is2xxSuccessful()){
            throw new GlobalCustomException(ErrorCode.FORBIDDEN);
        }

        // 1회 출금 한도 천만원
        if(request.getPerWithdrawalLimit().compareTo(BigDecimal.valueOf(10000000.00)) > 0){
            throw new GlobalCustomException(ErrorCode.PER_WITHDRAWAL_LIMIT_EXCEEDED);
        }

        Account account = accountRepository.findById(accountId)
                .filter(a -> !a.getIsDelete() && a.getStatus().equals(AccountStatus.ACTIVE))
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        account.updatePerWithdrawalLimit(request.getPerWithdrawalLimit());
    }

    /**
     * 계좌 하루 출금 한도액 재설정
     */
    @LogDataChange
    @Transactional
    public void updateDailyWithdrawalLimit(UUID accountId, DailyWithdrawalLimitRequestDto request, UUID userId, String role) {

        if(role.equals(UserRole.CUSTOMER.name()) && !productService.findByAccountId(accountId, userId, role).getStatusCode().is2xxSuccessful()){
            throw new GlobalCustomException(ErrorCode.FORBIDDEN);
        }

        // 하루 출금 한도 오천만원
        if(request.getDailyWithdrawalLimit().compareTo(BigDecimal.valueOf(50000000.00)) > 0){
            throw new GlobalCustomException(ErrorCode.DAILY_WITHDRAWAL_LIMIT_EXCEEDED);
        }

        Account account = accountRepository.findById(accountId)
                .filter(a -> !a.getIsDelete() && a.getStatus().equals(AccountStatus.ACTIVE))
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        account.updateDailyWithdrawalLimit(request.getDailyWithdrawalLimit());
    }

    /**
     * 계좌 1회 이체 한도액 재설정
     */
    @LogDataChange
    @Transactional
    public void updatePerTransferLimit(UUID accountId, PerTransferLimitRequestDto request, UUID userId, String role) {

        if(role.equals(UserRole.CUSTOMER.name()) && !productService.findByAccountId(accountId, userId, role).getStatusCode().is2xxSuccessful()){
            throw new GlobalCustomException(ErrorCode.FORBIDDEN);
        }

        // 1회 이체 한도 일억원
        if(request.getPerTransferLimit().compareTo(BigDecimal.valueOf(100000000.00)) > 0){
            throw new GlobalCustomException(ErrorCode.PER_TRANSFER_LIMIT_EXCEEDED);
        }

        Account account = accountRepository.findById(accountId)
                .filter(a -> !a.getIsDelete() && a.getStatus().equals(AccountStatus.ACTIVE))
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        account.updatePerWithdrawalLimit(request.getPerTransferLimit());
    }

    /**
     * 계좌 하루 이체 한도액 재설정
     */
    @LogDataChange
    @Transactional
    public void updateDailyTransferLimit(UUID accountId, DailyTransferLimitRequestDto request, UUID userId, String role) {

        if(role.equals(UserRole.CUSTOMER.name()) && !productService.findByAccountId(accountId, userId, role).getStatusCode().is2xxSuccessful()){
            throw new GlobalCustomException(ErrorCode.FORBIDDEN);
        }

        // 하루 이체 한도 오억원
        if(request.getDailyTransferLimit().compareTo(BigDecimal.valueOf(500000000.00)) > 0){
            throw new GlobalCustomException(ErrorCode.DAILY_TRANSFER_LIMIT_EXCEEDED);
        }

        Account account = accountRepository.findById(accountId)
                .filter(a -> !a.getIsDelete() && a.getStatus().equals(AccountStatus.ACTIVE))
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        account.updateDailyWithdrawalLimit(request.getDailyTransferLimit());
    }


    /**
     * 계좌 해지
     * 고객은 본인만 계좌 해지 가능.
     */
    @LogDataChange
    @Transactional
    public Boolean deleteAccount(UUID accountId, UUID userId, String username, String role) {

        try {
            Account account = accountRepository.findById(accountId)
                    .filter(a -> !a.getIsDelete())
                    .orElseThrow(() -> new GlobalCustomException(ErrorCode.ACCOUNT_NOT_FOUND));

            if(account.getBalance().compareTo(BigDecimal.ZERO) ==0) {
                account.updateAccountStatus(AccountStatus.CLOSED);
                account.delete(username);
                return true;
            }

            return false;
        } catch (Exception e) {
            // 트랜잭션에 실패하거나 예외가 발생한 경우 false를 반환
            log.error("계좌 해지 실패: {}", e.getMessage());
            return false;
        }
    }


    /**
     * 대출 계좌 해지
     * 원래 대출 이자만큼 추가해 확인해야하지만 시간 관계상 구현 못함
     */
    @LogDataChange
    @Transactional
    public Boolean deleteLoanAccount(UUID accountId, BigDecimal amount, UUID userId, String username, String role) {

        try {
            Account account = accountRepository.findById(accountId)
                    .filter(a -> !a.getIsDelete())
                    .orElseThrow(() -> new GlobalCustomException(ErrorCode.ACCOUNT_NOT_FOUND));

            if(account.getBalance().compareTo(amount) == 0){
                account.updateAccountStatus(AccountStatus.CLOSED);
                account.delete(username);
                return true;
            }

            return false;
        } catch (Exception e) {
            // 트랜잭션에 실패하거나 예외가 발생한 경우 false를 반환
            log.error("대출 계좌 해지 실패: {}", e.getMessage());
            return false;
        }
    }


    /**
     * 계좌 전체 조회
     * 전체 조회 때도 @Transactional(readOnly = true)를 붙이는가?
     * TODO: 고객 본인의 계좌 전체 조회 가능
     */
    @LogDataChange
    @Transactional(readOnly = true)
    public Page<AccountListResponseDto> getAccounts(AccountSearchRequestDto search, Pageable pageable) {

        return accountRepository.searchAccounts(search, pageable);
    }


    // 계좌 상세 조회
    @LogDataChange
    @Transactional(readOnly = true)
    public AccountResponseDto getAccount(UUID accountId, UUID userId, String role) {

        if(role.equals(UserRole.CUSTOMER.name()) && !productService.findByAccountId(accountId, userId, role).getStatusCode().is2xxSuccessful()) {
            throw new GlobalCustomException(ErrorCode.FORBIDDEN);
        }

        Account account = accountRepository.findById(accountId)
                .filter(a -> !a.getIsDelete())
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.ACCOUNT_NOT_FOUND));

            return accountMapper.toDto(account);
    }


    // 계좌 비밀번호 확인 및 입력 시도 제한
    @LogDataChange
    @Transactional
    public void checkAccountPin(UUID accountId, String accountPin) {

        // 비밀번호가 6자리인지 확인
        if (accountPin == null || accountPin.length() != 6) {
            throw new GlobalCustomException(ErrorCode.INVALID_ACCOUNT_PIN_LENGTH);
        }

        String redisKey = PIN_FAILURE_KEY_PREFIX + accountId;

        // 레디스에서 실패 횟수 조회
        Integer pinFailureCount = (Integer) redisTemplate.opsForValue().get(redisKey);
        if (pinFailureCount == null) {
            pinFailureCount = 0;
        }

        Account account = accountRepository.findById(accountId)
                .filter(a -> !a.getIsDelete() && a.getStatus().equals(AccountStatus.ACTIVE))
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        if (!account.getAccountPin().equals(accountPin)) {
            // 실패 횟수 증가
            redisTemplate.opsForValue().increment(redisKey);
            // TTL 설정
            redisTemplate.expire(redisKey, Duration.ofSeconds(ONE_WEEK_IN_SECONDS));
            pinFailureCount++;
            throw new GlobalCustomException(ErrorCode.ACCOUNT_PIN_NOT_MATCH);
        }

        // 3회 넘게 실패 시 계좌 잠금 처리
        if (pinFailureCount > 3) {
            account.updateAccountStatus(AccountStatus.LOCKED);
            throw new GlobalCustomException(ErrorCode.ACCOUNT_LOCKED); // 계좌 잠김 처리
        }

        // 비밀번호가 맞으면 레디스에서 실패 횟수 초기화
        redisTemplate.delete(redisKey);
    }


    // 계좌 비밀번호 초기화 및 계좌 상태 활성화
    @Transactional
    public void resetAccountPin(UUID accountId, ResetAccountPinRequestDto request, UUID userId, String role) {

        if(role.equals(UserRole.CUSTOMER.name()) && !productService.findByAccountId(accountId, userId, role).getStatusCode().is2xxSuccessful()){
            throw new GlobalCustomException(ErrorCode.FORBIDDEN);
        }

        if(!request.getChangeAccountPin().equals(request.getCheckAccountPin())) {
            throw new GlobalCustomException(ErrorCode.CHECK_ACCOUNT_PIN_NOT_MATCH);
        }

        // 계좌 정보 조회
        Account account = accountRepository.findById(accountId)
                .filter(a -> !a.getIsDelete() && a.getStatus().equals(AccountStatus.LOCKED))
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        // 임시로 비밀번호를 000000으로 초기화
        account.updateAccountPin("000000");

        account.updateAccountPin(request.getChangeAccountPin());

        // 비밀번호 변경이 성공하면 계좌 활성화
        account.updateAccountStatus(AccountStatus.ACTIVE);

        // Redis에서 실패 횟수 초기화
        String redisKey = PIN_FAILURE_KEY_PREFIX + accountId;
        redisTemplate.delete(redisKey);  // Redis에서 실패 횟수 삭제

        log.info("Account PIN has been reset and account is deactivated for accountId: {}", accountId);
    }

    // Redis 키 생성 메서드 (예: 계좌번호 + 날짜)
    private String getDailyWithdrawalKey(String accountNumber) {
        LocalDate today = LocalDate.now();
        return "withdrawal_limit:" + accountNumber + ":" + today;
    }

    // 출금 한도액 검사
    public void checkWithdrawalLimit(Account account, BigDecimal withdrawalAmount) {

        // 1회 출금 한도 체크
        if (withdrawalAmount.compareTo(account.getPerWithdrawalLimit()) > 0) {
            throw new GlobalCustomException(ErrorCode.PER_WITHDRAWAL_LIMIT_EXCEEDED);
        }

        String key = getDailyWithdrawalKey(account.getAccountNumber());

        // Redis에서 그날의 출금 누적 금액 조회
        BigDecimal currentWithdrawals = (BigDecimal) redisTemplate.opsForValue().get(key);

        if (currentWithdrawals == null) {
            currentWithdrawals = BigDecimal.ZERO;
        }

        // 하루 출금 한도 체크
        BigDecimal newTotalWithdrawals = currentWithdrawals.add(withdrawalAmount);
        if (newTotalWithdrawals.compareTo(account.getDailyWithdrawalLimit()) > 0) {
            throw new GlobalCustomException(ErrorCode.DAILY_WITHDRAWAL_LIMIT_EXCEEDED);
        }

        // Redis에 누적 출금 금액 업데이트, TTL은 그날의 자정까지 설정
        redisTemplate.opsForValue().set(key, newTotalWithdrawals);

        // 자정까지 TTL 설정
        long ttl = ChronoUnit.SECONDS.between(LocalDateTime.now(), LocalDate.now().plusDays(1).atStartOfDay());
        redisTemplate.expire(key, ttl, TimeUnit.SECONDS);
    }

    // Redis 키 생성 메서드 (예: 계좌번호 + 날짜)
    private String getDailyTransferKey(String accountNumber) {
        LocalDate today = LocalDate.now();
        return "transfer_limit:" + accountNumber + ":" + today;
    }

    // 이체 한도액 검사
    public void checkTransferLimit(Account account, BigDecimal transferAmount) {

        // 1회 이체 한도 체크
        if (transferAmount.compareTo(account.getPerTransferLimit()) > 0) {
            throw new GlobalCustomException(ErrorCode.PER_TRANSFER_LIMIT_EXCEEDED);
        }

        String key = getDailyTransferKey(account.getAccountNumber());

        // Redis에서 그날의 이체 누적 금액 조회
        BigDecimal currentTransfers = (BigDecimal) redisTemplate.opsForValue().get(key);

        if (currentTransfers == null) {
            currentTransfers = BigDecimal.ZERO;
        }

        // 하루 이체 한도 체크
        BigDecimal newTotalTransfers = currentTransfers.add(transferAmount);
        if (newTotalTransfers.compareTo(account.getDailyTransferLimit()) > 0) {
            throw new GlobalCustomException(ErrorCode.DAILY_TRANSFER_LIMIT_EXCEEDED);
        }

        // Redis에 누적 출금 금액 업데이트, TTL은 그날의 자정까지 설정
        redisTemplate.opsForValue().set(key, newTotalTransfers);

        // 자정까지 TTL 설정
        long ttl = ChronoUnit.SECONDS.between(LocalDateTime.now(), LocalDate.now().plusDays(1).atStartOfDay());
        redisTemplate.expire(key, ttl, TimeUnit.SECONDS);
    }
}