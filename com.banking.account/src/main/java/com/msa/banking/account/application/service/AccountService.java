package com.msa.banking.account.application.service;

import com.msa.banking.account.domain.model.Account;
import com.msa.banking.account.domain.repository.AccountRepository;
import com.msa.banking.account.infrastructure.accountgenerator.AccountNumberGenerator;
import com.msa.banking.account.presentation.dto.account.AccountRequestDto;
import com.msa.banking.account.presentation.dto.account.AccountResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }


    // 계좌 생성
    // TODO: 계좌의 경우 읽기, 쓰기가 모두 비슷한 빈도로 자주 발생할 것으로 예상. 분산 데이터베이스 설계를 고려할 필요성이 있음.
    @Transactional
    public AccountResponseDto createAccount(String accountNumber, AccountRequestDto requestDto, String username) {

        accountNumber = AccountNumberGenerator.generateAccountNumber();
        while(accountRepository.findByAccountNumber(accountNumber).isPresent()){
            accountNumber = AccountNumberGenerator.generateAccountNumber();
        }
        Account account = Account.createAccount(accountNumber, requestDto, username);
        accountRepository.save(account);

        return
    }


    // 계좌 수정


    // 계좌 삭제


    // 계좌 전체 조회


    // 계좌 상세 조회



}