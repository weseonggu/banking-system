package com.msa.banking.account.application.service;

import com.msa.banking.account.application.mapper.TransactionsMapper;
import com.msa.banking.account.domain.model.Account;
import com.msa.banking.account.domain.model.AccountTransactions;
import com.msa.banking.account.domain.model.TransactionStatus;
import com.msa.banking.account.domain.repository.AccountRepository;
import com.msa.banking.account.domain.repository.TransactionsRepository;
import com.msa.banking.account.presentation.dto.transactions.TransactionRequestDto;
import com.msa.banking.account.presentation.dto.transactions.TransactionResponseDto;
import com.msa.banking.account.presentation.dto.transactions.TransactionsListResponseDto;
import com.msa.banking.account.presentation.dto.transactions.TransactionsSearchRequestDto;
import com.msa.banking.common.base.UserRole;
import com.msa.banking.common.response.ErrorCode;
import com.msa.banking.commonbean.annotation.LogDataChange;
import com.msa.banking.commonbean.exception.GlobalCustomException;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class TransactionsService {

    private final TransactionsRepository transactionsRepository;
    private final AccountRepository accountRepository;
    private final TransactionsMapper transactionsMapper;
    private final StringEncryptor encryptor;

    @Autowired
    public TransactionsService(TransactionsRepository transactionsRepository, AccountRepository accountRepository, TransactionsMapper transactionsMapper, StringEncryptor encryptor) {
        this.transactionsRepository = transactionsRepository;
        this.accountRepository = accountRepository;
        this.transactionsMapper = transactionsMapper;
        this.encryptor = encryptor;
    }


    // TODO: 입금 기능이 필요한가? 비밀번호를 3번 이상 틀리면 본인 인증 확인 필요.
    // TODO: 거래 발생 시 거래 알림
    /**
     * 출금 + 결제 기능

     **/
    @LogDataChange
    @Transactional
    public TransactionResponseDto createWithdrawal(UUID accountId, String accountPin, TransactionRequestDto request, String username, String role) {

        // 출금하려는 계좌 찾기
        Account account = accountRepository.findById(accountId)
                .filter(p -> !p.getIsDelete())
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        // 출금하기
        // 계좌 잔액 체크
        // BigDecimal은 객체이기 때문에, 값을 비교하려면 compareTo() 메서드를 사용해야 한다.
        if(request.amount().compareTo(account.getBalance()) > 0) {
            throw new GlobalCustomException(ErrorCode.WITHDRAWAL_NOT_POSSIBLE);
        }

        // 비밀번호 확인 (출금 전에 바로 처리)
        checkAccountPin(accountId, accountPin);

        // 계좌 거래 생성
        AccountTransactions transaction =
                AccountTransactions.createSenderTransaction(account, request);

        BigDecimal totalBalance =  account.getBalance().subtract(request.amount());
        account.updateAccount(totalBalance);

        // 계좌 잔액과 해당 계좌 거래 내역 합산 비교
        if(!account.getBalance().equals(transactionsRepository.getTotalBalance(accountId))){
            transaction.updateTransactionStatus(TransactionStatus.FAILED);
            throw new GlobalCustomException(ErrorCode.BALANCE_NOT_MATCH);
        } else {
            transaction.updateTransactionStatus(TransactionStatus.COMPLETED);
        }

        transactionsRepository.save(transaction);
        return transactionsMapper.toDto(transaction);
    }


    /**
     * 이체 기능(저축 + 대출 상환 포함 가능)
     **/
    @LogDataChange
    @Transactional
    public void createTransafer(UUID accountId,String accountPin, TransactionRequestDto request, String username, String role) {

        String encryptedPin = encryptor.encrypt(accountPin);

        // 송금인의 계좌 찾기
        Account senderAccount = accountRepository.findById(accountId)
                .filter(p -> !p.getIsDelete())
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        // 수취인의 계좌 찾기
        Account beneficiaryAccount = accountRepository.findByAccountNumber(request.beneficiaryAccount())
                .filter(p -> !p.getIsDelete())
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        // 계좌 잔액 체크
        if(request.amount().compareTo(senderAccount.getBalance()) > 0) {
            throw new GlobalCustomException(ErrorCode.WITHDRAWAL_NOT_POSSIBLE);
        }

        // 송금인 비밀번호 확인 (출금 전에 바로 처리)
        checkAccountPin(accountId, accountPin);

        /**
         * 송금인, 수취인 계좌 거래 생성 시 하나라도 실패하면 둘다 롤백해야함.
         **/
        // 송금인 계좌 거래 생성
        AccountTransactions senderTransaction =
                AccountTransactions.createSenderTransaction(senderAccount, request);

        // 수취인 계좌 거래 생성
        AccountTransactions beneficiaryTransaction =
                AccountTransactions.createBeneficiaryTransaction(beneficiaryAccount, senderAccount.getAccountNumber(), request);

        // 송금인 계좌 이체 금액 차감
        BigDecimal totalSenderBalance =  senderAccount.getBalance().subtract(request.amount());
        senderAccount.updateAccount(totalSenderBalance);

        // 수취인 계좌 이체 금액 추가
        BigDecimal totalBeneficiaryBalance =  beneficiaryAccount.getBalance().add(request.amount());
        beneficiaryAccount.updateAccount(totalBeneficiaryBalance);

        // 송금인/수취인 계좌 잔액과 해당 계좌 거래 내역 합산 비교
        if(!senderAccount.getBalance().equals(transactionsRepository.getTotalBalance(accountId)) ||
            !beneficiaryAccount.getBalance().equals(transactionsRepository.getTotalBalance(beneficiaryAccount.getAccountId())))
        {
            senderTransaction.updateTransactionStatus(TransactionStatus.FAILED);
            beneficiaryTransaction.updateTransactionStatus(TransactionStatus.FAILED);
            throw new GlobalCustomException(ErrorCode.BALANCE_NOT_MATCH);
        } else {
            senderTransaction.updateTransactionStatus(TransactionStatus.COMPLETED);
            beneficiaryTransaction.updateTransactionStatus(TransactionStatus.COMPLETED);
        }

        transactionsRepository.save(senderTransaction);
        transactionsRepository.save(beneficiaryTransaction);
    }


    @LogDataChange
    public void checkAccountPin(UUID accountId, String accountPin) {

        // 비밀번호 암호화
        String encryptedPin = encryptor.encrypt(accountPin);

        // 비밀번호 확인
        if ( accountRepository.getAccountPin(accountId).equals(encryptedPin)) {
            throw new GlobalCustomException(ErrorCode.ACCOUNTPIN_NOT_MATCH);
        }
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
}
