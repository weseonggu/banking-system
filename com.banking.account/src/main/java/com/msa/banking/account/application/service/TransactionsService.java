package com.msa.banking.account.application.service;

import com.msa.banking.account.application.mapper.TransactionsMapper;
import com.msa.banking.account.domain.model.Account;
import com.msa.banking.account.domain.model.AccountTransactions;
import com.msa.banking.account.domain.model.TransactionStatus;
import com.msa.banking.account.domain.repository.AccountRepository;
import com.msa.banking.account.domain.repository.TransactionsRepository;
import com.msa.banking.account.presentation.dto.transactions.TransactionResponseDto;
import com.msa.banking.account.presentation.dto.transactions.TransactionsListResponseDto;
import com.msa.banking.account.presentation.dto.transactions.TransactionsSearchRequestDto;
import com.msa.banking.common.base.UserRole;
import com.msa.banking.common.response.ErrorCode;
import com.msa.banking.commonbean.annotation.LogDataChange;
import com.msa.banking.commonbean.exception.GlobalCustomException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class TransactionsService {

    private final TransactionsRepository transactionsRepository;
    private final AccountRepository accountRepository;
    private final TransactionsMapper transactionsMapper;

    public TransactionsService(TransactionsRepository transactionsRepository, AccountRepository accountRepository, TransactionsMapper transactionsMapper) {
        this.transactionsRepository = transactionsRepository;
        this.accountRepository = accountRepository;
        this.transactionsMapper = transactionsMapper;
    }


    /**
     * 입금 기능(저축 + 대출 상환 포함 가능)
        최종 잔액 조회 시 전체 잔액을 합산 후 반영
     **/
    @LogDataChange
    @Transactional
    public TransactionResponseDto createDeposit(UUID accountId, String username, String role) {

        Account account = accountRepository.findById(accountId)
                .filter(p -> !p.getIsDelete())
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        // 고객의 경우 본인만이 계좌 입금 거래 가능
        if(role.equals(UserRole.CUSTOMER.name()) && !username.equals(account.getCreatedBy())){
            throw new GlobalCustomException(ErrorCode.FORBIDDEN);
        } else {

        }
    }

    /**
     * 출금 + 결제 기능

     **/
//    @LogDataChange
//    @Transactional
//    public TransactionResponseDto createWithdrawal(UUID accountId) {
//
//
//    }

    /**
     * 이체 기능(저축 + 대출 상환 포함 가능)

     **/



    // TODO: 계좌 거래 상태를 본인이 수정하는가? 거래가 이루어진 상태를 분류해서 로직을 다시 설정해야함.
    // TODO: 거래 실패 시 롤백, 내부에서 호출.
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
