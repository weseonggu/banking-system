package com.msa.banking.account.application.service;

import com.msa.banking.account.application.event.EventProducer;
import com.msa.banking.account.application.mapper.TransactionsMapper;
import com.msa.banking.account.domain.model.Account;
import com.msa.banking.account.domain.model.AccountTransactions;
import com.msa.banking.account.domain.repository.AccountRepository;
import com.msa.banking.account.domain.repository.TransactionsRepository;
import com.msa.banking.account.infrastructure.redisson.RedissonLock;
import com.msa.banking.account.presentation.dto.transactions.TransactionsListResponseDto;
import com.msa.banking.account.presentation.dto.transactions.TransactionsSearchRequestDto;
import com.msa.banking.account.presentation.dto.transactions.TransferTransactionRequestDto;
import com.msa.banking.account.presentation.dto.transactions.WithdrawalTransactionRequestDto;
import com.msa.banking.common.account.dto.DepositTransactionRequestDto;
import com.msa.banking.common.account.dto.TransactionResponseDto;
import com.msa.banking.common.account.dto.TransactionStatus;
import com.msa.banking.common.account.type.AccountStatus;
import com.msa.banking.common.base.UserRole;
import com.msa.banking.common.response.ErrorCode;
import com.msa.banking.commonbean.annotation.LogDataChange;
import com.msa.banking.commonbean.exception.GlobalCustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Log4j2
@RequiredArgsConstructor
public class TransactionsService {

    private final TransactionsRepository transactionsRepository;
    private final AccountRepository accountRepository;
    private final TransactionsMapper transactionsMapper;
    private final EventProducer eventProducer;
    private final StringEncryptor encryptor;

    // TODO: 모든 금융 거래에서 본인의 계좌일 경우에만 거래가능하게 함.
    // TODO: 계좌가 휴면에 들면 입금하면 바로 활성화상태로 사용이 가능한가? 아니면 다른 확인 절차가 필요한가?
    /**
     * 입금 기능
     **/
    @LogDataChange
    @RedissonLock(value = "#accountId.toString()") // accountId로 락 적용
    @Transactional
    public TransactionResponseDto createDeposit(UUID accountId, DepositTransactionRequestDto request, String username, String role) {

        // 입금하려는 계좌 찾기
        Account account = accountRepository.findById(accountId)
                .filter(p -> !p.getIsDelete() && p.getStatus().equals(AccountStatus.ACTIVE))
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        // 입금 처리
        AccountTransactions depositTransaction = AccountTransactions.createSingleDepositTransaction(account, request);

        BigDecimal newBalance = account.getBalance().add(request.depositAmount());
        account.updateAccountBalance(newBalance);

        accountRepository.save(account);
        transactionsRepository.save(depositTransaction);

        return transactionsMapper.toDto(depositTransaction);
    }

    // TODO: 입금 기능이 필요한가? 비밀번호를 3번 이상 틀리면 본인 인증 확인 필요.
    // TODO: 거래 발생 시 거래 알림
    // TODO: 계좌 거래 합산과 계좌 잔액 비교를 BATCH로 처리. 실시간 처리에서는 생략.-> 일치하지 않으면 에러 처리는 어떻게?
    /**
     * 출금 + 결제 기능
     **/
    @LogDataChange
    @RedissonLock(value = "#accountId.toString()")
    @Transactional
    public TransactionResponseDto createWithdrawal(UUID accountId, WithdrawalTransactionRequestDto request, UUID userId, String role) {

        // 출금하려는 계좌 찾기
        Account account = accountRepository.findById(accountId)
                .filter(p -> !p.getIsDelete() && p.getStatus().equals(AccountStatus.ACTIVE))
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        // 출금하기
        if (request.withdrawalAmount().compareTo(account.getBalance()) > 0) {
            throw new GlobalCustomException(ErrorCode.WITHDRAWAL_NOT_POSSIBLE);
        }

        // 비밀번호 확인
        checkAccountPin(accountId, request.accountPin());

        // 계좌 거래 생성
        AccountTransactions withdrawalTransaction = AccountTransactions.createSingleWithdrawalTransaction(account, request);
        transactionsRepository.save(withdrawalTransaction);

        // 금액 차감
        BigDecimal totalBalance = account.getBalance().subtract(request.withdrawalAmount());
        account.updateAccountBalance(totalBalance);
        accountRepository.save(account);

        // Kafka 이벤트 전송
        eventProducer.sendTransactionCreatedEvent(accountId, userId, role, withdrawalTransaction);

        return transactionsMapper.toDto(withdrawalTransaction);
    }


    // TODO: 이체 시 description null이면 송금인 이름으로 대체
    /**
     * 이체 기능(저축 + 대출 상환 포함 가능)
     **/
    @LogDataChange
    @RedissonLock(value = {"#accountId.toString()", "#request.beneficiaryAccount()"})  // 송금인과 수취인 계좌에 대해 각각 락 적용
    @Transactional
    public void createTransfer(UUID accountId, TransferTransactionRequestDto request, UUID userId, String role) {


        validateAccountNumberFormat(request.beneficiaryAccount());
        // 비즈니스 로직
        Account senderAccount = accountRepository.findById(accountId)
                .filter(p -> !p.getIsDelete() && p.getStatus().equals(AccountStatus.ACTIVE))
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        Account beneficiaryAccount = accountRepository.findByAccountNumber(request.beneficiaryAccount())
                .filter(p -> !p.getIsDelete() && p.getStatus().equals(AccountStatus.ACTIVE))
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        // 계좌 잔액 체크
        if (request.amount().compareTo(senderAccount.getBalance()) > 0) {
            throw new GlobalCustomException(ErrorCode.WITHDRAWAL_NOT_POSSIBLE);
        }

        // 송금인 비밀번호 확인
        checkAccountPin(accountId, request.accountPin());

        // 송금인 계좌 거래 생성
        AccountTransactions senderTransaction = AccountTransactions.createSenderTransaction(senderAccount, request);
        transactionsRepository.save(senderTransaction);

        // 수취인 계좌 거래 생성
        AccountTransactions beneficiaryTransaction = AccountTransactions.createBeneficiaryTransaction(beneficiaryAccount, senderAccount.getAccountNumber(), request);
        transactionsRepository.save(beneficiaryTransaction);

        // 금액 차감 및 저장
        BigDecimal totalSenderBalance = senderAccount.getBalance().subtract(request.amount());
        senderAccount.updateAccountBalance(totalSenderBalance);
        accountRepository.save(senderAccount);

        // 수취인 계좌 금액 추가
        BigDecimal totalBeneficiaryBalance = beneficiaryAccount.getBalance().add(request.amount());
        beneficiaryAccount.updateAccountBalance(totalBeneficiaryBalance);
        accountRepository.save(beneficiaryAccount);

        // Kafka 이벤트 전송
        eventProducer.sendTransactionCreatedEvent(accountId, userId, role, senderTransaction);
    }



    // TODO: 계좌 거래 상태를 본인이 수정하는가? 거래가 이루어진 상태를 분류해서 로직을 다시 설정해야함.
    // 거래 상태 수정
    @LogDataChange
    @Transactional
    public TransactionResponseDto updateTransactionStatus(Long transactionId, TransactionStatus status, String username, String role){

        AccountTransactions transaction = transactionsRepository.findById(transactionId)
                .filter(p -> !p.getIsDelete())
                .orElseThrow(()-> new GlobalCustomException(ErrorCode.TRANSACTION_NOT_FOUND));

        if(role.equals(UserRole.CUSTOMER.name()) && !username.equals(transaction.getCreatedBy())){
            throw new GlobalCustomException(ErrorCode.FORBIDDEN);
        } else {
            transaction.updateTransactionStatus(status);
            return transactionsMapper.toDto(transaction);
        }
    }


    // 거래 설명 수정
    @LogDataChange
    @Transactional
    public TransactionResponseDto updateTransaction(Long transactionId, String description, String username, String role){

        AccountTransactions transaction = transactionsRepository.findById(transactionId)
                .filter(p -> !p.getIsDelete())
                .orElseThrow(()-> new GlobalCustomException(ErrorCode.TRANSACTION_NOT_FOUND));

        if(role.equals(UserRole.CUSTOMER.name()) && !username.equals(transaction.getCreatedBy())){
            throw new GlobalCustomException(ErrorCode.FORBIDDEN);
        } else {
            transaction.updateTransaction(description);
            return transactionsMapper.toDto(transaction);
        }
    }


    // TODO: 자신의 계좌 거래를 조회가 가능해야 함.
    // 거래 전체 조회
    @LogDataChange
    @Transactional(readOnly = true)
    public Page<TransactionsListResponseDto> getTransactions(TransactionsSearchRequestDto search, Pageable pageable) {

        return transactionsRepository.searchTransactions(search, pageable);
    }


    // 거래 상세 조회
    @LogDataChange
    @Transactional(readOnly = true)
    public TransactionResponseDto getTransaction(Long transactionId, String username, String role) {

        AccountTransactions transaction = transactionsRepository.findById(transactionId)
                .filter(p -> !p.getIsDelete())
                .orElseThrow(()-> new GlobalCustomException(ErrorCode.TRANSACTION_NOT_FOUND));

        if(role.equals(UserRole.CUSTOMER.name()) && !username.equals(transaction.getCreatedBy())){
            throw new GlobalCustomException(ErrorCode.FORBIDDEN);
        } else {
            return transactionsMapper.toDto(transaction);
        }
    }

    // 계좌 비밀번호 확인
    @LogDataChange
    public void checkAccountPin(UUID accountId, String accountPin) {

        // 비밀번호 암호화
        String encryptedPin = encryptor.encrypt(accountPin);

        // 비밀번호 확인
        if ( accountRepository.getAccountPin(accountId).equals(encryptedPin)) {
            throw new GlobalCustomException(ErrorCode.ACCOUNTPIN_NOT_MATCH);
        }
    }

    // 계좌 번호 검증
    @LogDataChange
    public void validateAccountNumberFormat(String accountNumber) {
        if (!accountNumber.matches("\\d{3}-\\d{4}-\\d{7}")) {
            throw new IllegalArgumentException("계좌번호는 xxx-xxxx-xxxxxxx 형식을 따라야 합니다.");
        }
    }
}
