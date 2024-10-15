package com.msa.banking.account.application.service;

import com.msa.banking.account.application.mapper.TransactionsMapper;
import com.msa.banking.account.domain.model.Account;
import com.msa.banking.account.domain.model.AccountTransactions;
import com.msa.banking.account.domain.repository.AccountRepository;
import com.msa.banking.account.domain.repository.TransactionsRepository;
import com.msa.banking.account.infrastructure.redisson.RedissonLock;
import com.msa.banking.account.presentation.dto.transactions.*;
import com.msa.banking.common.account.dto.DepositTransactionRequestDto;
import com.msa.banking.common.account.dto.SingleTransactionResponseDto;
import com.msa.banking.common.account.type.AccountStatus;
import com.msa.banking.common.account.type.TransactionType;
import com.msa.banking.common.base.UserRole;
import com.msa.banking.common.response.ErrorCode;
import com.msa.banking.commonbean.annotation.LogDataChange;
import com.msa.banking.commonbean.exception.GlobalCustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Log4j2
@RequiredArgsConstructor
public class AccountTransactionsService {

    private final TransactionalService transactionalService;
    private final TransactionsRepository transactionsRepository;
    private final AccountRepository accountRepository;
    private final TransactionsMapper transactionsMapper;


    /**
     * 일반적인 입금을 제외한 금융 거래에서 본인의 계좌일 경우에만 거래가능하게 함.
     * TODO: 계좌 거래 발생 시 거래 알림
     * 계좌가 휴면 상태가 되면 인증 절차를 거쳐 계좌를 활성화시키는게 일반적이기 때문에 일단 활성화된 계좌에 한해 금융 거래가 가능하다.
     * 입금 기능
     */
    @LogDataChange
    @RedissonLock(value = "#request.accountNumber()") // accountNumber로 락 적용
    public SingleTransactionResponseDto createDeposit(DepositTransactionRequestDto request) {

        // 거래 상태 확인
        if(!request.type().equals(TransactionType.DEPOSIT) && !request.type().equals(TransactionType.SAVINGS_DEPOSIT) &&
                !request.type().equals(TransactionType.LOAN_REPAYMENT)) {
            throw new GlobalCustomException(ErrorCode.INVALID_TRANSACTION_TYPE);
        }

        // 입금액 확인
        if(request.depositAmount()==null || request.depositAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new GlobalCustomException(ErrorCode.AMOUNT_BAD_REQUEST);
        }

        // 입금하려는 계좌 찾기
        Account account = accountRepository.findByAccountNumber(request.accountNumber())
                .filter(a -> !a.getIsDelete() && a.getStatus().equals(AccountStatus.ACTIVE))
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.ACCOUNT_NOT_FOUND));


        // 입금 처리
        AccountTransactions depositTransaction = transactionalService.createDepositTransaction(account, request);
        transactionalService.updateDepositAccountBalance(account, request);
        return transactionsMapper.toDto(depositTransaction);
    }

    /**
     * 대출액 입금 기능
     * 맨 처음 한도액을 정할때만 기능 작동 가능.
     * 본인 소유의 계좌가 아니면 기능 작동 불가.
     */
    @LogDataChange
    @RedissonLock(value = "#accountId.toString()") // accountId로 락 적용
    public SingleTransactionResponseDto createLoanDeposit(UUID accountId, DepositTransactionRequestDto request, String username, String role) {

        // 거래 상태 확인
        if(!request.type().equals(TransactionType.DEPOSIT)) {
            throw new GlobalCustomException(ErrorCode.INVALID_TRANSACTION_TYPE);
        }

        // 입금액 확인
        if(request.depositAmount()==null || request.depositAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new GlobalCustomException(ErrorCode.AMOUNT_BAD_REQUEST);
        }

        // 입금하려는 대출 계좌 찾기
        Account account = accountRepository.findById(accountId)
                .filter(a -> !a.getIsDelete() && a.getStatus().equals(AccountStatus.ACTIVE))
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        if(role.equals(UserRole.CUSTOMER.name()) && !username.equals(account.getCreatedBy())) {
            throw new GlobalCustomException(ErrorCode.FORBIDDEN);
        }

        // 입금 처리
        AccountTransactions depositTransaction = transactionalService.createDepositTransaction(account, request);
        transactionalService.updateDepositAccountBalance(account, request);
        return transactionsMapper.toDto(depositTransaction);
    }

    /**
     * 출금 + 결제 기능
     * 비밀번호를 3번 이상 틀리면 본인 인증 확인 필요.
     * TODO: 계좌 거래 합산과 계좌 잔액 비교를 BATCH로 처리. 실시간 처리에서는 생략.-> 일치하지 않으면 에러 처리는 어떻게?
     */
    @LogDataChange
    @RedissonLock(value = "#accountId.toString()")
    public SingleTransactionResponseDto createWithdrawal(UUID accountId, WithdrawalTransactionRequestDto request, UUID userId, String role) {

        // 거래 상태 확인
        if(!request.type().equals(TransactionType.WITHDRAWAL) && !request.type().equals(TransactionType.PAYMENT)) {
            throw new GlobalCustomException(ErrorCode.INVALID_TRANSACTION_TYPE);
        }

        // 출금액 확인
        if(request.withdrawalAmount() ==null || request.withdrawalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new GlobalCustomException(ErrorCode.AMOUNT_BAD_REQUEST);
        }

        // 출금하려는 계좌 찾기
        Account account = accountRepository.findById(accountId)
                .filter(a -> !a.getIsDelete() && a.getStatus().equals(AccountStatus.ACTIVE))
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        // 출금하기
        if (request.withdrawalAmount().compareTo(account.getBalance()) > 0) {
            throw new GlobalCustomException(ErrorCode.WITHDRAWAL_NOT_POSSIBLE);
        }

        // 비밀번호 확인
        checkAccountPin(accountId, request.accountPin());

        // 거래 내역 생성 및 저장
        AccountTransactions withdrawalTransaction = transactionalService.createWithdrawalTransaction(account, request);
        transactionalService.updateWithdrawalAccountBalance(account, request, accountId, userId, role, withdrawalTransaction);

        return transactionsMapper.toDto(withdrawalTransaction);
    }

    /**
     * 이체 기능(저축 + 대출 상환 포함 가능)
     * TODO: 이체 시 description null이면 송금인 이름으로 대체
     */
    @LogDataChange
    @RedissonLock(value = {"#accountId.toString()", "#request.beneficiaryAccount()"})  // 송금인과 수취인 계좌에 대해 각각 락 적용
    public TransferTransactionResponseDto createTransfer(UUID accountId, TransferTransactionRequestDto request, UUID userId, String role) {

        // 거래 상태 확인
        if(!request.type().equals(TransactionType.TRANSFER)) {
            throw new GlobalCustomException(ErrorCode.INVALID_TRANSACTION_TYPE);
        }

        // 송금액 확인
        if(request.amount() ==null || request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new GlobalCustomException(ErrorCode.AMOUNT_BAD_REQUEST);
        }

        validateAccountNumberFormat(request.beneficiaryAccount());
        // 비즈니스 로직
        Account senderAccount = accountRepository.findById(accountId)
                .filter(a -> !a.getIsDelete() && a.getStatus().equals(AccountStatus.ACTIVE))
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        Account beneficiaryAccount = accountRepository.findByAccountNumber(request.beneficiaryAccount())
                .filter(a -> !a.getIsDelete() && a.getStatus().equals(AccountStatus.ACTIVE))
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        // 계좌 잔액 체크
        if (request.amount().compareTo(senderAccount.getBalance()) > 0) {
            throw new GlobalCustomException(ErrorCode.WITHDRAWAL_NOT_POSSIBLE);
        }

        // 송금인 비밀번호 확인
        checkAccountPin(accountId, request.accountPin());

        // 거래 내역 생성 및 저장 (트랜잭션 분리)
        TransferAccountTransactions transferTransactions = transactionalService.createTransferTransactions(senderAccount, beneficiaryAccount, request);

         // 계좌 상태(잔액) 변경 및 Kafka 이벤트 전송
        transactionalService.updateTransferAccountBalances(senderAccount, beneficiaryAccount, request, transferTransactions.getSender(), accountId, userId, role);
        SenderTransactionResponseDto senderDto = transactionsMapper.toSenderDto(transferTransactions.getSender());
        BeneficiaryTransactionResponseDto beneficiaryDto = transactionsMapper.toBeneficiaryDto(transferTransactions.getReceiver());

        return new TransferTransactionResponseDto(senderDto, beneficiaryDto);
    }

    // 거래 설명 수정
    @LogDataChange
    @Transactional
    public SingleTransactionResponseDto updateTransaction(Long transactionId, String description, String username, String role){

        AccountTransactions transaction = transactionsRepository.findById(transactionId)
                .filter(a -> !a.getIsDelete())
                .orElseThrow(()-> new GlobalCustomException(ErrorCode.TRANSACTION_NOT_FOUND));

        if(role.equals(UserRole.CUSTOMER.name()) && !username.equals(transaction.getCreatedBy())){
            throw new GlobalCustomException(ErrorCode.FORBIDDEN);
        } else {
            transaction.updateTransactionDescription(description);
            return transactionsMapper.toDto(transaction);
        }
    }

    // TODO: 자신의 계좌 거래 조회가 가능해야 함.
    // 거래 전체 조회
    @LogDataChange
    @Transactional(readOnly = true)
    public Page<TransactionsListResponseDto> getTransactions(TransactionsSearchRequestDto search, Pageable pageable) {

        return transactionsRepository.searchTransactions(search, pageable);
    }

    // 거래 상세 조회
    @LogDataChange
    @Transactional(readOnly = true)
    public SingleTransactionResponseDto getTransaction(Long transactionId, String username, String role) {

        AccountTransactions transaction = transactionsRepository.findById(transactionId)
                .filter(a -> !a.getIsDelete())
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

        // 비밀번호가 6자리인지 확인
        if (accountPin == null || accountPin.length() != 6) {
            throw new GlobalCustomException(ErrorCode.INVALID_ACCOUNT_PIN_LENGTH);
        }

        // 비밀번호 확인
        if (!accountRepository.getAccountPin(accountId).equals(accountPin)) {
            throw new GlobalCustomException(ErrorCode.ACCOUNT_PIN_NOT_MATCH);
        }
    }

    // 계좌 번호 검증
    @LogDataChange
    public void validateAccountNumberFormat(String accountNumber) {
        if (!accountNumber.matches("\\d{3}-\\d{4}-\\d{7}")) {
            throw new GlobalCustomException(ErrorCode.INVALID_ACCOUNT_FORMAT);
        }
    }
}
