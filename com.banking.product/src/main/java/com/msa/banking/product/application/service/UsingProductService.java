package com.msa.banking.product.application.service;

import com.msa.banking.common.account.dto.AccountRequestDto;
import com.msa.banking.common.account.type.AccountStatus;
import com.msa.banking.common.account.type.AccountType;
import com.msa.banking.common.base.UserRole;
import com.msa.banking.commonbean.security.UserDetailsImpl;
import com.msa.banking.product.domain.model.CheckingInUse;
import com.msa.banking.product.domain.model.UsingProduct;
import com.msa.banking.product.domain.repository.UsingProductRepositoryCustom;
import com.msa.banking.product.infrastructure.client.AccountClient;
import com.msa.banking.product.infrastructure.repository.UsingProductRepository;
import com.msa.banking.product.lib.ProductType;
import com.msa.banking.product.presentation.request.RequsetJoinChecking;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsingProductService {

    private final AccountClient accountClient;
    private final UsingProductRepository usingProductRepository;

    public void joinChecking(RequsetJoinChecking requsetJoinChecking, UserDetailsImpl userDetails) {

        // 요청자 확인 직원일 경우 가능, 일반 사용자일경우 userDetails랑 dto의 id가 같은지 확인
        if(userDetails.getRole().equals(UserRole.CUSTOMER.getAuthority())){
            if(!userDetails.getUserId().equals(requsetJoinChecking.getUserId())){
                throw new IllegalArgumentException("타인의 아이디로 가입 할 수 없습니다.");
            }
        }
        // 계좌 생성 요청 -> 응답으로 계좌 id(UUID)를 반환 예외처리
//        AccountRequestDto requestDto = new AccountRequestDto(requsetJoinChecking.getName(),
//                AccountStatus.ACTIVE,
//                AccountType.CHECKING,
//                requsetJoinChecking.getAccountPin());
//
//        ResponseEntity<UUID> response = accountClient.addAccount(requestDto);

        ResponseEntity<UUID> response =  new ResponseEntity<>(UUID.randomUUID(), HttpStatus.OK);// 임시 계좌 id 생성

        // 엔티티 생성 UsingProduct, CeckingInUse 생성
        CheckingInUse checkingInUse = CheckingInUse.create(requsetJoinChecking.getInterestRate(), requsetJoinChecking.getFeeWaiver());
        UsingProduct usingProduct = UsingProduct.create(requsetJoinChecking.getUserId(), ProductType.CHECKING, response.getBody(), requsetJoinChecking.getName());
        usingProduct.addChckingInuse(checkingInUse);

        // DB에저장
        usingProductRepository.save(usingProduct);
        // 성공 응답

    }
}
