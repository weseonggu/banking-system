package com.msa.banking.account.application.service;

import com.msa.banking.account.application.mapper.AccountMapper;
import com.msa.banking.account.domain.model.Account;
import com.msa.banking.account.domain.repository.AccountRepository;
import com.msa.banking.account.infrastructure.accountgenerator.AccountNumberGenerator;
import com.msa.banking.account.presentation.dto.account.AccountListResponseDto;
import com.msa.banking.account.presentation.dto.account.AccountSearchRequestDto;
import com.msa.banking.common.account.dto.AccountRequestDto;
import com.msa.banking.common.account.dto.AccountResponseDto;
import com.msa.banking.common.account.type.AccountStatus;
import com.msa.banking.common.base.UserRole;
import com.msa.banking.common.response.ErrorCode;
import com.msa.banking.commonbean.annotation.LogDataChange;
import com.msa.banking.commonbean.exception.GlobalCustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final ProductService productService;

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
    public void updateAccountPin(UUID accountId, String pin,  UUID userId, String role) {

        // 고객의 경우 본인 만이 계좌 비밀번호 변경 가능
        if(role.equals(UserRole.CUSTOMER.name()) && !productService.findByAccountId(accountId, userId, role).getStatusCode().is2xxSuccessful()){
            throw new GlobalCustomException(ErrorCode.FORBIDDEN);
        }

        Account account = accountRepository.findById(accountId)
                .filter(a -> !a.getIsDelete())
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        account.updateAccountPin(pin);
    }

    /**
     * 계좌 해지
     * 고객은 본인만 계좌 해지 가능.
     */
    @LogDataChange
    @Transactional
    public void deleteAccount(UUID accountId, UUID userId, String username, String role) {

        if(role.equals(UserRole.CUSTOMER.name()) && !productService.findByAccountId(accountId, userId, role).getStatusCode().is2xxSuccessful()) {
            throw new GlobalCustomException(ErrorCode.FORBIDDEN);
        }

        Account account = accountRepository.findById(accountId)
                .filter(a -> !a.getIsDelete())
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        account.updateAccountStatus(AccountStatus.CLOSED);
        account.delete(username);
    }

    /**
     * 대출 계좌 해지
     */
    @LogDataChange
    @Transactional
    public void deleteLoanAccount(UUID accountId, UUID userId, String username, String role) {

        if(role.equals(UserRole.CUSTOMER.name()) && !productService.findByAccountId(accountId, userId, role).getStatusCode().is2xxSuccessful()) {
            throw new GlobalCustomException(ErrorCode.FORBIDDEN);
        }

        Account account = accountRepository.findById(accountId)
                .filter(a -> !a.getIsDelete())
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.ACCOUNT_NOT_FOUND));


        account.updateAccountStatus(AccountStatus.CLOSED);
        account.delete(username);
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
}