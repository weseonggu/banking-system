package com.msa.banking.account.application.service;

import com.msa.banking.account.application.event.EventProducer;
import com.msa.banking.account.application.event.TransactionStatusEvent;
import com.msa.banking.account.domain.model.Account;
import com.msa.banking.account.domain.model.AccountTransactions;
import com.msa.banking.account.domain.repository.AccountRepository;
import com.msa.banking.account.domain.repository.TransactionsRepository;
import com.msa.banking.account.presentation.dto.transactions.DepositTransactionRequestDto;
import com.msa.banking.common.account.dto.LoanDepositTransactionRequestDto;
import com.msa.banking.account.presentation.dto.transactions.TransferAccountTransactions;
import com.msa.banking.account.presentation.dto.transactions.TransferTransactionRequestDto;
import com.msa.banking.account.presentation.dto.transactions.WithdrawalTransactionRequestDto;
import com.msa.banking.common.response.ErrorCode;
import com.msa.banking.commonbean.exception.GlobalCustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
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


    // 대출 계좌 대출액 거래 내역 생성
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AccountTransactions createDepositTransaction(Account account, LoanDepositTransactionRequestDto request) {

        AccountTransactions loanDepositTransaction;

        try {
            // 계좌 거래 생성
            loanDepositTransaction = AccountTransactions.createLoanDepositTransaction(account, request);

            if(request.getDescription()==null){
                loanDepositTransaction.updateTransactionDescription("대출액 입금");
            }

            transactionsRepository.save(loanDepositTransaction);
        } catch (Exception e) {
            throw new GlobalCustomException(ErrorCode.ACCOUNT_TRANSACTION_FAILED);
        }

        // 거래 상태 이벤트 발행
        eventPublisher.publishEvent(new TransactionStatusEvent(loanDepositTransaction.getTransactionId()));

        return loanDepositTransaction;
    }


    // 입금 트랜잭션 내에서 잔액 변경 처리 및 Kafka 이벤트 전송
    @Transactional
    public void updateDepositAccountBalance(Account account, BigDecimal depositAmount, UUID userId, String role, AccountTransactions depositTransaction) {

        try {
            // 금액 추가
            BigDecimal newBalance = account.getBalance().add(depositAmount);
            account.updateAccountBalance(newBalance);

            accountRepository.save(account);
        } catch (Exception e) {
            throw new GlobalCustomException(ErrorCode.BALANCE_TRANSACTION_FAILED);
        }

        eventProducer.sendTransactionCreatedEvent(account.getAccountId(), userId, role, depositTransaction);
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
    public void updateWithdrawalAccountBalance(Account account, BigDecimal withdrawalAmount, UUID accountId, UUID userId, String role, AccountTransactions withdrawalTransaction) {

        try {
            // 금액 차감
            BigDecimal totalBalance = account.getBalance().subtract(withdrawalAmount);
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
    public void updateTransferAccountBalances(Account senderAccount, Account beneficiaryAccount, BigDecimal transferAmount, AccountTransactions senderTransaction, UUID accountId, UUID userId, String role) {

        try {
            // 송금인 금액 차감 및 저장
            BigDecimal totalSenderBalance = senderAccount.getBalance().subtract(transferAmount);
            senderAccount.updateAccountBalance(totalSenderBalance);

            accountRepository.save(senderAccount);

            // 수취인 계좌 금액 추가 및 저장
            BigDecimal totalBeneficiaryBalance = beneficiaryAccount.getBalance().add(transferAmount);
            beneficiaryAccount.updateAccountBalance(totalBeneficiaryBalance);

            accountRepository.save(beneficiaryAccount);
        } catch (Exception e) {
            throw new GlobalCustomException(ErrorCode.BALANCE_TRANSACTION_FAILED);
        }

        // 모든 작업이 성공하면 Kafka 이벤트 전송
        eventProducer.sendTransactionCreatedEvent(accountId, userId, role, senderTransaction);
    }
}
