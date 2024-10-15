package com.msa.banking.account.application.service;

import com.msa.banking.account.application.mapper.DirectDebitMapper;
import com.msa.banking.account.domain.model.Account;
import com.msa.banking.account.domain.model.DirectDebit;
import com.msa.banking.account.domain.model.DirectDebitStatus;
import com.msa.banking.account.domain.repository.AccountRepository;
import com.msa.banking.account.domain.repository.DirectDebitRepository;
import com.msa.banking.account.presentation.dto.directDebit.DirectDebitListResponseDto;
import com.msa.banking.account.presentation.dto.directDebit.DirectDebitRequestDto;
import com.msa.banking.account.presentation.dto.directDebit.DirectDebitResponseDto;
import com.msa.banking.account.presentation.dto.directDebit.DirectDebitSearchRequestDto;
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
public class DirectDebitService {

    private final DirectDebitRepository directDebitRepository;
    private final AccountRepository accountRepository;
    private final DirectDebitMapper directDebitMapper;
    private final ProductService productService;



    // 자동 이체 등록
    @LogDataChange
    @Transactional
    public DirectDebitResponseDto createDirectDebit(
            UUID accountId, DirectDebitRequestDto request, UUID userId, String role) {

        // 고객의 경우 본인만이 자동 이체 등록 가능
        if(role.equals(UserRole.CUSTOMER.name()) && !productService.findByAccountId(accountId, userId, role).getStatusCode().is2xxSuccessful()){
            throw new GlobalCustomException(ErrorCode.FORBIDDEN);
        }

        // 이체 날짜 검증
        if(!isValidTransferDay(request.transferDate())){
            throw new GlobalCustomException(ErrorCode.TRANSFER_DATE_NOT_AVAILABLE);
        }

        Account account = accountRepository.findById(accountId)
                .filter(a -> !a.getIsDelete())
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.ACCOUNT_NOT_FOUND));


        DirectDebit directDebit = DirectDebit.createDirectDebit(account, request);
        directDebitRepository.save(directDebit);
        return directDebitMapper.toDto(directDebit);
    }


    // TODO: 매니저나 마스터가 자동 이체를 생성할 시 고객의 권한 체크를 어떻게 할 것인가?
    // 자동 이체 수정
    @LogDataChange
    @Transactional
    public DirectDebitResponseDto updateDirectDebit(
            UUID directDebitId, DirectDebitRequestDto request, UUID userId, String role) {

        DirectDebit directDebit = directDebitRepository.findById(directDebitId)
                .filter(a -> !a.getIsDelete())
                .orElseThrow(()-> new GlobalCustomException(ErrorCode.DIRECT_DEBIT_NOT_FOUND));

        // 고객의 경우 본인만이 자동 이체 수정 가능
        if(role.equals(UserRole.CUSTOMER.name()) &&!productService.findByAccountId(directDebit.getAccount().getAccountId(), userId, role).getStatusCode().is2xxSuccessful()){
            throw new GlobalCustomException(ErrorCode.FORBIDDEN);
        }

        if(!isValidTransferDay(request.transferDate())){
            throw new GlobalCustomException(ErrorCode.TRANSFER_DATE_NOT_AVAILABLE);
        }

        directDebit.updateDirectDebit(request);
        return directDebitMapper.toDto(directDebit);

    }

    // TODO: Status도 변경해야 하는데 현재 delete로는 함께 변경 불가, 따로 status만 변경하는 메서드를 생성해야 하는가?
    // 자동 이체 해지
    @LogDataChange
    @Transactional
    public void deleteDirectDebit(UUID directDebitId, UUID userId, String role) {

        DirectDebit directDebit = directDebitRepository.findById(directDebitId)
                .filter(a -> !a.getIsDelete())
                .orElseThrow(()-> new GlobalCustomException(ErrorCode.DIRECT_DEBIT_NOT_FOUND));

        if(role.equals(UserRole.CUSTOMER.name()) &&!productService.findByAccountId(directDebit.getAccount().getAccountId(), userId, role).getStatusCode().is2xxSuccessful()){
            throw new GlobalCustomException(ErrorCode.FORBIDDEN);
        }

        directDebit.updateDirectDebitStatus(DirectDebitStatus.CANCELLED);
        directDebitRepository.delete(directDebit);
    }


    // TODO: 자신이 등록한 자동이체는 봐야함. 자동 이체 조회에 대한 권한 로직을 세분화할 필요성이 있음
    // 자동 이체 전체 조회
    @LogDataChange
    @Transactional(readOnly = true)
    public Page<DirectDebitListResponseDto> getDirectDebits(DirectDebitSearchRequestDto search, Pageable pageable){

        return directDebitRepository.searchDirectDebits(search, pageable);
    }

    // 자동 이체 상세 조회
    @LogDataChange
    @Transactional(readOnly = true)
    public DirectDebitResponseDto getDirectDebit(UUID directDebitId, UUID userId, String role) {

        DirectDebit directDebit = directDebitRepository.findById(directDebitId)
                .filter(a -> !a.getIsDelete())
                .orElseThrow(()-> new GlobalCustomException(ErrorCode.DIRECT_DEBIT_NOT_FOUND));

        if(role.equals(UserRole.CUSTOMER.name()) &&!productService.findByAccountId(directDebit.getAccount().getAccountId(), userId, role).getStatusCode().is2xxSuccessful()){
            throw new GlobalCustomException(ErrorCode.FORBIDDEN);
        }

        return directDebitMapper.toDto(directDebit);
    }

    // TODO: 자동이체 계좌를 스케줄러로 이체
    // 날짜 검증
    public boolean isValidTransferDay(int dayOfMonth) {
        // dayOfMonth 값이 1~31 사이인지 먼저 확인
        if (dayOfMonth < 1 || dayOfMonth > 31) return false;
        else return true;
    }
}
