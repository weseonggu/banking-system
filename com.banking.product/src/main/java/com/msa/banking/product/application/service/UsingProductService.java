package com.msa.banking.product.application.service;

import com.msa.banking.product.presentation.request.RequsetJoinChecking;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsingProductService {

    public void joinChcking(RequsetJoinChecking requsetJoinChecking) {

        // 요청자 확인 직원일 경우 가능, 일반 사용자일경우 userDetails랑 dto의 id가 같은지 확인

        // 계좌 생성 요청 -> 응답으로 계좌 id(UUID)를 반환

        // 엔티티 생성 UsingProduct, CeckingInUse 생성

        // DB에저장

        // 성공 응답

    }
}
