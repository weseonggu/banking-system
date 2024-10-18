package com.msa.banking.account.application.service;

import com.msa.banking.account.application.event.EventProducer;
import com.msa.banking.account.application.event.TransactionStatusEvent;
import com.msa.banking.account.domain.model.Account;
import com.msa.banking.account.domain.model.AccountTransactions;
import com.msa.banking.account.domain.repository.AccountRepository;
import com.msa.banking.account.domain.repository.TransactionsRepository;
import com.msa.banking.common.account.dto.DepositTransactionRequestDto;
import com.msa.banking.account.presentation.dto.transactions.TransferAccountTransactions;
import com.msa.banking.account.presentation.dto.transactions.TransferTransactionRequestDto;
import com.msa.banking.account.presentation.dto.transactions.WithdrawalTransactionRequestDto;
import com.msa.banking.common.account.type.AccountStatus;
import com.msa.banking.common.response.ErrorCode;
import com.msa.banking.commonbean.annotation.LogDataChange;
import com.msa.banking.commonbean.exception.GlobalCustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Log4j2
@RequiredArgsConstructor
public class TransactionalService {

    private final TransactionsRepository transactionsRepository;
    private final AccountRepository accountRepository;
    private final EventProducer eventProducer;
    private final ApplicationEventPublisher eventPublisher;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String PIN_FAILURE_KEY_PREFIX = "accountPin:failure:";
    private static final long ONE_WEEK_IN_SECONDS = 604800; // 7일을 초로 변환한 값


    // 입금 계좌 거래 내역 생성
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AccountTransactions createDepositTransaction(Account account, DepositTransactionRequestDto request) {

        AccountTransactions depositTransaction;

        try {
            // 계좌 거래 생성
            depositTransaction = AccountTransactions.createSingleDepositTransaction(account, request);
            // 거래 설명 없을 시
            if(request.getDescription()==null){
                depositTransaction.updateTransactionDescription("입금");
            }
            transactionsRepository.save(depositTransaction);
        } catch (Exception e) {
            throw new GlobalCustomException(ErrorCode.ACCOUNT_TRANSACTION_FAILED);
        }

        // 거래 상태 이벤트 발행
        eventPublisher.publishEvent(new TransactionStatusEvent(depositTransaction.getTransactionId()));
        return depositTransaction;
    }


    // 입금 트랜잭션 내에서 잔액 변경 처리 및 Kafka 이벤트 전송
    @Transactional
    public void updateDepositAccountBalance(Account account,  DepositTransactionRequestDto request) {

        try {
            // 금액 추가
            BigDecimal newBalance = account.getBalance().add(request.getDepositAmount());
            account.updateAccountBalance(newBalance);
            accountRepository.save(account);
        } catch (Exception e) {
            throw new GlobalCustomException(ErrorCode.BALANCE_TRANSACTION_FAILED);
        }
    }


    // 출금 계좌 거래 내역 생성
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AccountTransactions createWithdrawalTransaction(Account account, WithdrawalTransactionRequestDto request) {

        AccountTransactions withdrawalTransaction;

        try {
            // 계좌 거래 생성
            withdrawalTransaction = AccountTransactions.createSingleWithdrawalTransaction(account, request);
            // 거래 설명 없을 시
            if(request.getDescription()==null){
                withdrawalTransaction.updateTransactionDescription("출금");
            }
            transactionsRepository.save(withdrawalTransaction);
        } catch (Exception e) {
            throw new GlobalCustomException(ErrorCode.ACCOUNT_TRANSACTION_FAILED);
        }

        // 거래 상태 이벤트 발행
        eventPublisher.publishEvent(new TransactionStatusEvent(withdrawalTransaction.getTransactionId()));
        return withdrawalTransaction;
    }


    // 출금 트랜잭션 내에서 잔액 변경 처리 및 Kafka 이벤트 전송
    @Transactional
    public void updateWithdrawalAccountBalance(Account account, WithdrawalTransactionRequestDto request, UUID accountId, UUID userId, String role, AccountTransactions withdrawalTransaction) {

        try {
            // 금액 차감
            BigDecimal totalBalance = account.getBalance().subtract(request.getWithdrawalAmount());
            account.updateAccountBalance(totalBalance);
            accountRepository.save(account);
        } catch (Exception e) {
            throw new GlobalCustomException(ErrorCode.BALANCE_TRANSACTION_FAILED);
        }

        // Kafka 이벤트 전송
        eventProducer.sendTransactionCreatedEvent(accountId, userId, role, withdrawalTransaction);
    }


    // 이체 계좌 거래 내역 생성
    @Transactional(propagation = Propagation.REQUIRES_NEW)  // 별도의 트랜잭션으로 처리
    public TransferAccountTransactions createTransferTransactions(Account senderAccount, Account beneficiaryAccount, TransferTransactionRequestDto request) {

        AccountTransactions senderTransaction;
        AccountTransactions beneficiaryTransaction;

        try {
            // 송금인 계좌 거래 생성
            senderTransaction = AccountTransactions.createSenderTransaction(senderAccount, request);
            // 거래 설명 없을 시
            if(request.getDescription()==null){
                senderTransaction.updateTransactionDescription(beneficiaryAccount.getAccountHolder());
            }
            transactionsRepository.save(senderTransaction);

            // 수취인 계좌 거래 생성
            beneficiaryTransaction = AccountTransactions.createBeneficiaryTransaction(beneficiaryAccount, senderAccount.getAccountNumber(), request);
            // 거래 설명 없을 시
            if(request.getDescription()==null){
                beneficiaryTransaction.updateTransactionDescription(senderAccount.getAccountHolder());
            }
            transactionsRepository.save(beneficiaryTransaction);
        } catch(Exception e) {
            throw new GlobalCustomException(ErrorCode.ACCOUNT_TRANSACTION_FAILED);
        }

        // 이벤트 발행(현재 PENDING 상태)
        eventPublisher.publishEvent(new TransactionStatusEvent(senderTransaction.getTransactionId()));
        eventPublisher.publishEvent(new TransactionStatusEvent(beneficiaryTransaction.getTransactionId()));

        return new TransferAccountTransactions(senderTransaction, beneficiaryTransaction);
    }


    // 이체 트랜잭션 내에서 잔액 변경 처리 및 Kafka 이벤트 전송
    @Transactional
    public void updateTransferAccountBalances(Account senderAccount, Account beneficiaryAccount, TransferTransactionRequestDto request, AccountTransactions senderTransaction, UUID accountId, UUID userId, String role) {

        try {
            // 송금인 금액 차감 및 저장
            BigDecimal totalSenderBalance = senderAccount.getBalance().subtract(request.getAmount());
            senderAccount.updateAccountBalance(totalSenderBalance);
            accountRepository.save(senderAccount);

            // 수취인 계좌 금액 추가 및 저장
            BigDecimal totalBeneficiaryBalance = beneficiaryAccount.getBalance().add(request.getAmount());
            beneficiaryAccount.updateAccountBalance(totalBeneficiaryBalance);
            accountRepository.save(beneficiaryAccount);
        } catch (Exception e) {
            throw new GlobalCustomException(ErrorCode.BALANCE_TRANSACTION_FAILED);
        }

        // 모든 작업이 성공하면 Kafka 이벤트 전송
        eventProducer.sendTransactionCreatedEvent(accountId, userId, role, senderTransaction);
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

        // 3회 넘게 실패 시 계좌 잠금 처리
        if (pinFailureCount > 3) {
            account.updateAccountStatus(AccountStatus.LOCKED);
            throw new GlobalCustomException(ErrorCode.ACCOUNT_LOCKED); // 계좌 잠김 처리
        }

        if (!account.getAccountPin().equals(accountPin)) {
            // 실패 횟수 증가
            redisTemplate.opsForValue().increment(redisKey);
            pinFailureCount++;
            throw new GlobalCustomException(ErrorCode.ACCOUNT_PIN_NOT_MATCH);
        }

        // 비밀번호가 맞으면 레디스에서 실패 횟수 초기화
        redisTemplate.delete(redisKey);
    }

    // 계좌 비밀번호 초기화 및 계좌 상태 활성화
    @Transactional
    public void resetAccountPin(UUID accountId, String accountPin, String checkPin, UUID userId, String role) {

        // 비밀번호가 6자리인지 확인
        if (accountPin == null || accountPin.length() != 6) {
            throw new GlobalCustomException(ErrorCode.INVALID_ACCOUNT_PIN_LENGTH);
        }

        // 계좌 정보 조회
        Account account = accountRepository.findById(accountId)
                .filter(a -> !a.getIsDelete())
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        // 임시로 비밀번호를 000000으로 초기화
        account.updateAccountPin("000000");

        if(accountPin.equals(checkPin)) {
            account.updateAccountPin(accountPin);
            // 비밀번호 변경이 성공하면 계좌 활성화
            account.updateAccountStatus(AccountStatus.ACTIVE);
        } else {
            throw new GlobalCustomException(ErrorCode.ACCOUNT_PIN_NOT_MATCH);
        }

        // Redis에서 실패 횟수 초기화
        String redisKey = PIN_FAILURE_KEY_PREFIX + accountId;
        redisTemplate.delete(redisKey);  // Redis에서 실패 횟수 삭제

        log.info("Account PIN has been reset and account is deactivated for accountId: {}", accountId);
    }
}
