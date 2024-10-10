package com.msa.banking.account.application.service;

import com.msa.banking.account.application.mapper.AccountMapper;
import com.msa.banking.account.domain.model.Account;
import com.msa.banking.common.account.type.AccountStatus;
import com.msa.banking.account.domain.repository.AccountRepository;
import com.msa.banking.account.infrastructure.accountgenerator.AccountNumberGenerator;
import com.msa.banking.account.presentation.dto.account.AccountListResponseDto;
import com.msa.banking.common.account.dto.AccountRequestDto;
import com.msa.banking.common.account.dto.AccountResponseDto;
import com.msa.banking.account.presentation.dto.account.AccountSearchRequestDto;
import com.msa.banking.common.base.UserRole;
import com.msa.banking.common.response.ErrorCode;
import com.msa.banking.commonbean.annotation.LogDataChange;
import com.msa.banking.commonbean.exception.GlobalCustomException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    public AccountService(AccountRepository accountRepository, AccountMapper accountMapper) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
    }


    // 계좌 등록
    // TODO: 계좌의 경우 읽기, 쓰기가 모두 비슷한 빈도로 자주 발생할 것으로 예상. 분산 데이터베이스 설계를 고려할 필요성이 있음.
    // TODO: 고객이 아닌 MANAGER가 계좌를 생성시에는 CREATEDBY를 고객의 USERNAME으로 설정해야함.
    @LogDataChange
    @Transactional
    public UUID createAccount(AccountRequestDto request, String username) {

        // TODO: 실소유자명이 사용자의 실명과 일치하는지 체크
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



    // TODO: 장기 휴면 시 계좌 상태를 어떻게 변경할 지 관건. 스케줄러 이용? 매니저가 변경?
    // 계좌 상태 변경
    @LogDataChange
    @Transactional
    public AccountResponseDto updateAccountStatus(UUID accountId, AccountStatus status, String username, String role) {

        Account account = accountRepository.findById(accountId)
                .filter(p -> !p.getIsDelete())
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        // 고객의 경우 본인만이 계좌 상태 수정 가능
        if(role.equals(UserRole.CUSTOMER.name()) && !username.equals(account.getCreatedBy())){
            throw new GlobalCustomException(ErrorCode.FORBIDDEN);
        } else {
            account.updateAccountStatus(status);
            // mapstruct 라이브러리를 사용하여 entity -> dto 변환
            return accountMapper.toDto(account);
        }
    }


    // TODO: 계좌 비밀번호 변경 시 로직을 어떻게 짤 것인지 고민. 본인인지 확인하는 검증 로직
    // 계좌 비밀번호 변경
    @LogDataChange
    @Transactional
    public void updateAccountPin(UUID accountId, String pin, String username, String role) {

        Account account = accountRepository.findById(accountId)
                .filter(p -> !p.getIsDelete())
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        // 고객의 경우 본인만이 계좌 상태 수정 가능
        if(role.equals(UserRole.CUSTOMER.name()) && !username.equals(account.getCreatedBy())){
            throw new GlobalCustomException(ErrorCode.FORBIDDEN);
        } else {
            account.updateAccountPin(pin);
        }
    }


    // TODO: AccountStatus도 함께 변경해야하는데 해당 delete는 불가. 따로 status만 변경하는 메서드를 생서해야하는가?
    // 계좌 해지
    @LogDataChange
    @Transactional
    public void deleteAccount(UUID accountId, String username, String role) {

        Account account = accountRepository.findById(accountId)
                .filter(p -> !p.getIsDelete())
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        // 고객의 경우 본인만이 계좌 상태 해지 가능
        if(role.equals(UserRole.CUSTOMER.name()) && !username.equals(account.getCreatedBy())){
            throw new GlobalCustomException(ErrorCode.FORBIDDEN);
        } else {
            account.delete(username);
        }
    }

    // 계좌 전체 조회
    @LogDataChange
    @Transactional(readOnly = true) // TODO: 전체 조회 때도 readOnly를 붙이는가?
    public Page<AccountListResponseDto> getAccounts(AccountSearchRequestDto search, Pageable pageable) {

        return accountRepository.searchAccounts(search, pageable);
    }

    // 계좌 상세 조회
    @LogDataChange
    @Transactional(readOnly = true)
    public AccountResponseDto getAccount(UUID accountId, String username, String role) {

        Account account = accountRepository.findById(accountId)
                .filter(p -> !p.getIsDelete())
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        // 고객의 경우 본인만이 계좌 조회 가능
        if(role.equals(UserRole.CUSTOMER.name()) && !username.equals(account.getCreatedBy())){
            throw new GlobalCustomException(ErrorCode.FORBIDDEN);
        } else {
            return accountMapper.toDto(account);
        }
    }

}