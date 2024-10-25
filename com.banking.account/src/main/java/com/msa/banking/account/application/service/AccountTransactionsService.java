package com.msa.banking.account.application.service;

import com.msa.banking.account.application.mapper.TransactionsMapper;
import com.msa.banking.account.domain.model.Account;
import com.msa.banking.account.domain.model.AccountTransactions;
import com.msa.banking.account.domain.repository.AccountRepository;
import com.msa.banking.account.domain.repository.TransactionsRepository;
import com.msa.banking.account.infrastructure.redisson.RedissonLock;
import com.msa.banking.account.presentation.dto.transactions.*;
import com.msa.banking.common.account.dto.LoanDepositTransactionRequestDto;
import com.msa.banking.common.account.dto.SingleTransactionResponseDto;
import com.msa.banking.common.account.type.AccountStatus;
import com.msa.banking.common.account.type.TransactionType;
import com.msa.banking.common.base.UserRole;
import com.msa.banking.common.response.ErrorCode;
import com.msa.banking.commonbean.annotation.LogDataChange;
import com.msa.banking.commonbean.exception.GlobalCustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Log4j2
@RequiredArgsConstructor
public class AccountTransactionsService {

    private final TransactionalService transactionalService;
    private final TransactionsRepository transactionsRepository;
    private final AccountRepository accountRepository;
    private final TransactionsMapper transactionsMapper;
    private final ProductService productService; // FeignClient를 상속한 서비스
    private final AccountService accountService;




    /**
     * 일반적인 입금을 제외한 금융 거래에서 본인의 계좌일 경우에만 거래가능하게 함.
     * TODO: 계좌 거래 발생 시 거래 알림
     * 계좌가 휴면 상태가 되면 인증 절차를 거쳐 계좌를 활성화시키는게 일반적이기 때문에 일단 활성화된 계좌에 한해 금융 거래가 가능하다.
     * 입금 기능
     */
    @LogDataChange
    public SingleTransactionResponseDto createDeposit(DepositTransactionRequestDto request) {

        // 거래 상태 확인
        if(!request.getType().equals(TransactionType.DEPOSIT) && !request.getType().equals(TransactionType.SAVINGS_DEPOSIT)) {
            throw new GlobalCustomException(ErrorCode.INVALID_TRANSACTION_TYPE);
        }

        // 입금하려는 계좌 찾기
        Account account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .filter(a -> !a.getIsDelete() && a.getStatus().equals(AccountStatus.ACTIVE))
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        // 락이 필요한 부분만 별도로 분리하여 처리
        return performLockedDeposit(account, request);
    }

    @RedissonLock(value = "#request.getAccountNumber()")
    private SingleTransactionResponseDto performLockedDeposit(Account account, DepositTransactionRequestDto request) {
        AccountTransactions depositTransaction = transactionalService.createDepositTransaction(account, request);
        transactionalService.updateDepositAccountBalance(account, request.getDepositAmount());
        return transactionsMapper.toDto(depositTransaction);
    }

    /**
     * 대출액 입금 기능
     * 맨 처음 한도액을 정할때만 기능 작동 가능.
     * 본인 소유의 계좌가 아니면 기능 작동 불가.
     */
    @LogDataChange
    @RedissonLock(value = "#accountId.toString()") // accountId로 락 적용
    public SingleTransactionResponseDto createLoanDeposit(UUID accountId, LoanDepositTransactionRequestDto request, UUID userId, String role) {

        // 거래 상태 확인
        if(!request.getType().equals(TransactionType.LOAN_DEPOSIT)) {
            throw new GlobalCustomException(ErrorCode.INVALID_TRANSACTION_TYPE);
        }

        // 입금하려는 대출 계좌 찾기
        Account account = accountService.findAccountById(accountId);

        // 입금 처리
        AccountTransactions depositTransaction = transactionalService.createDepositTransaction(account, request);
        transactionalService.updateDepositAccountBalance(account, request.getDepositAmount());
        return transactionsMapper.toDto(depositTransaction);
    }

    @Cacheable(value = "productStatusCache", key = "#accountId")
    public ResponseEntity<?> getProductStatus(UUID accountId, UUID userId, String userRole) {
        // FeignClient를 통해 ProductService 호출
        return productService.findByAccountId(accountId, userId, userRole);
    }

    /**
     * 출금 + 결제 기능
     * 비밀번호를 3번 이상 틀리면 본인 인증 확인 필요.
     */
    @LogDataChange
    public SingleTransactionResponseDto createWithdrawal(UUID accountId, WithdrawalTransactionRequestDto request, UUID userId, String role) {

        if(role.equals(UserRole.CUSTOMER.name()) && !getProductStatus(accountId, userId, role).getStatusCode().is2xxSuccessful()) {
            throw new GlobalCustomException(ErrorCode.FORBIDDEN);
        }

        // 거래 상태 확인
        if(!request.getType().equals(TransactionType.WITHDRAWAL) && !request.getType().equals(TransactionType.PAYMENT)) {
            throw new GlobalCustomException(ErrorCode.INVALID_TRANSACTION_TYPE);
        }

        // 출금하려는 계좌 찾기
        Account account = accountService.findAccountById(accountId);

        // 출금 한도 체크
        accountService.checkWithdrawalLimit(account, request.getWithdrawalAmount());

        // 락이 필요한 부분만 별도로 처리
        return performLockedWithdrawal(account, request, accountId, userId, role);
    }

    @RedissonLock(value = "#accountId.toString()")
    private SingleTransactionResponseDto performLockedWithdrawal(Account account, WithdrawalTransactionRequestDto request, UUID accountId, UUID userId, String role) {

        // 출금하기
        if (request.getWithdrawalAmount().compareTo(account.getBalance()) > 0) {
            throw new GlobalCustomException(ErrorCode.WITHDRAWAL_NOT_POSSIBLE);
        }

        // 비밀번호 확인
        accountService.checkAccountPin(accountId, request.getAccountPin());

        // 거래 내역 생성 및 저장
        AccountTransactions withdrawalTransaction = transactionalService.createWithdrawalTransaction(account, request);
        transactionalService.updateWithdrawalAccountBalance(account, request.getWithdrawalAmount(), accountId, userId, role, withdrawalTransaction);

        return transactionsMapper.toDto(withdrawalTransaction);
    }

    /**
     * 이체 기능(저축 + 대출 상환 포함 가능)
     */
    @LogDataChange
    public TransferTransactionResponseDto createTransfer(UUID accountId, TransferTransactionRequestDto request, UUID userId, String role) {

        if (role.equals(UserRole.CUSTOMER.name()) && !productService.findByAccountId(accountId, userId, role).getStatusCode().is2xxSuccessful()) {
            throw new GlobalCustomException(ErrorCode.FORBIDDEN);
        }

        // 거래 상태 확인
        if (!request.getType().equals(TransactionType.TRANSFER)) {
            throw new GlobalCustomException(ErrorCode.INVALID_TRANSACTION_TYPE);
        }

        // 송금인 계좌 조회 (트랜잭션 불필요)
        Account senderAccount = accountService.findAccountById(accountId);

        // 이체 한도 체크 (트랜잭션 불필요)
        accountService.checkTransferLimit(senderAccount, request.getAmount());

        // 수취인 계좌 조회 (트랜잭션 불필요)
        Account beneficiaryAccount = accountRepository.findByAccountNumber(request.getBeneficiaryAccount())
                .filter(a -> !a.getIsDelete() && (a.getStatus().equals(AccountStatus.ACTIVE) || a.getStatus().equals(AccountStatus.LOCKED)))
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        // 잔액 체크 (트랜잭션 불필요)
        if (request.getAmount().compareTo(senderAccount.getBalance()) > 0) {
            throw new GlobalCustomException(ErrorCode.WITHDRAWAL_NOT_POSSIBLE);
        }

        // 비밀번호 확인 (트랜잭션 불필요)
        accountService.checkAccountPin(accountId, request.getAccountPin());

        // 락이 필요한 부분만 별도로 처리
        return performLockedTransfer(senderAccount, beneficiaryAccount, request, accountId, userId, role);
    }

    // 트랜잭션 적용 부분
    @RedissonLock(value = {"#senderAccount.getAccountNumber()", "#beneficiaryAccount.getAccountNumber()"})
    @Transactional
    public TransferTransactionResponseDto performLockedTransfer(Account senderAccount, Account beneficiaryAccount, TransferTransactionRequestDto request, UUID accountId, UUID userId, String role) {

        // 거래 내역 생성 및 저장 (트랜잭션 적용됨)
        TransferAccountTransactions transferTransactions = transactionalService.createTransferTransactions(senderAccount, beneficiaryAccount, request);

        // 잔액 변경 및 Kafka 이벤트 전송 (트랜잭션 적용됨)
        transactionalService.updateTransferAccountBalances(senderAccount, beneficiaryAccount, request.getAmount(), transferTransactions.getSender(), accountId, userId, role);

        SenderTransactionResponseDto senderDto = transactionsMapper.toSenderDto(transferTransactions.getSender());
        BeneficiaryTransactionResponseDto beneficiaryDto = transactionsMapper.toBeneficiaryDto(transferTransactions.getReceiver());

        return new TransferTransactionResponseDto(senderDto, beneficiaryDto);
    }

    // 거래 설명 수정
    @LogDataChange
    @Transactional
    public SingleTransactionResponseDto updateTransaction(Long transactionId, String description, UUID userId, String role){

        AccountTransactions transaction = transactionsRepository.findById(transactionId)
                .filter(a -> !a.getIsDelete())
                .orElseThrow(()-> new GlobalCustomException(ErrorCode.TRANSACTION_NOT_FOUND));

        if(role.equals(UserRole.CUSTOMER.name()) &&!productService.findByAccountId(transaction.getAccount().getAccountId(), userId, role).getStatusCode().is2xxSuccessful()){
            throw new GlobalCustomException(ErrorCode.FORBIDDEN);
        }

        transaction.updateTransactionDescription(description);
        return transactionsMapper.toDto(transaction);
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
    public SingleTransactionResponseDto getTransaction(Long transactionId, UUID userId, String role) {

        AccountTransactions transaction = transactionsRepository.findById(transactionId)
                .filter(a -> !a.getIsDelete())
                .orElseThrow(()-> new GlobalCustomException(ErrorCode.TRANSACTION_NOT_FOUND));

        if(role.equals(UserRole.CUSTOMER.name()) &&!productService.findByAccountId(transaction.getAccount().getAccountId(), userId, role).getStatusCode().is2xxSuccessful()){
            throw new GlobalCustomException(ErrorCode.FORBIDDEN);
        }

        return transactionsMapper.toDto(transaction);
    }
}