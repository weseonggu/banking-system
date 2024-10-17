package com.msa.banking.product.presentation.controller.join;

import com.msa.banking.common.account.dto.AccountRequestDto;
import com.msa.banking.common.account.type.AccountStatus;
import com.msa.banking.common.account.type.AccountType;
import com.msa.banking.common.base.UserRole;
import com.msa.banking.commonbean.client.AuthClient;
import com.msa.banking.commonbean.security.UserDetailsImpl;
import com.msa.banking.product.domain.model.LoanInUse;
import com.msa.banking.product.domain.model.Product;
import com.msa.banking.product.domain.model.UsingProduct;
import com.msa.banking.product.infrastructure.client.AccountClient;
import com.msa.banking.product.infrastructure.client.AuthClient2;
import com.msa.banking.product.infrastructure.repository.ProductRepository;
import com.msa.banking.product.infrastructure.repository.UsingProductRepository;
import com.msa.banking.product.lib.ProductType;
import com.msa.banking.product.presentation.request.RequestJoinLoan;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
@Deprecated
@Service
@RequiredArgsConstructor
@Slf4j
public class JoinLoanLogic {

    private final AccountClient accountClient;
    private final UsingProductRepository usingProductRepository;
    private final ProductRepository productRepository;
    private final AuthClient authClient;
    private final AuthClient2 authClient2;


    @Transactional
    public UUID joinLoan(RequestJoinLoan requestJoinLoan, UserDetailsImpl userDetails) {

        // 중복 가입 여부 확인
        CompletableFuture<Boolean> checkDuplicate = CompletableFuture.supplyAsync(() ->
                usingProductRepository.existsByUserIdAndProductIdAndIsUsing(requestJoinLoan.getUserId(), requestJoinLoan.getProductId(), true)
        ).exceptionally(ex -> {
            log.error(ex.getMessage());
            throw new IllegalArgumentException("상품 중복 가입 확인 중 문제가 발생했습니다.");
        });

        // 상품 유효성 검사
        CompletableFuture<Product> productFuture = CompletableFuture.supplyAsync(() ->
                productRepository.findByIdWhereIsDeleted(requestJoinLoan.getProductId(), false, requestJoinLoan.getType(), LocalDateTime.now())
                        .orElseThrow(() -> new IllegalArgumentException("없거나 가입이 불가능한 상품입니다."))
        ).thenApply(product -> {
            if (!product.getType().equals(requestJoinLoan.getType())) {
                throw new IllegalArgumentException("입출금 상품 가입입니다. 다른 상품은 안됩니다.");
            }
            return product;
        });

        // 요청자 확인
        CompletableFuture<Void> checkUserRole = CompletableFuture.runAsync(() -> {
            if (userDetails.getRole().equals(UserRole.CUSTOMER.getAuthority())) {
                if (!userDetails.getUserId().equals(requestJoinLoan.getUserId())) {
                    throw new IllegalArgumentException("타인의 아이디로 가입 할 수 없습니다.");
                }
            }
        }).exceptionally(ex -> {
            log.error(ex.getMessage());
            throw new IllegalArgumentException("사용자 역할 확인 중 문제가 발생했습니다.");
        });

        // 사용자 정보 확인
        CompletableFuture<Boolean> checkUserInfo = CompletableFuture.supplyAsync(() ->
                checkUserInfo(requestJoinLoan.getUserId(),  requestJoinLoan.getName(), userDetails)
        ).exceptionally(ex -> {
            throw new IllegalArgumentException("사용자 정보 확인 중 문제가 발생했습니다.");
        });

        // 모든 CompletableFuture가 완료될 때까지 대기합니다.
        CompletableFuture<Void> allOf = CompletableFuture.allOf(checkDuplicate, productFuture, checkUserRole, checkUserInfo);
        allOf.join(); // 모든 검증 로직이 완료될 때까지 대기

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        // 계좌 생성 요청
        AccountRequestDto requestDto = new AccountRequestDto(requestJoinLoan.getName(),
                AccountStatus.ACTIVE,
                AccountType.CHECKING,
                requestJoinLoan.getAccountPin());

        ResponseEntity<UUID> response = accountClient.addAccount(requestDto);

        // 엔티티 생성
        LoanInUse loanInUse = LoanInUse.create(requestJoinLoan.getLoanAmount(), requestJoinLoan.getName(),
                requestJoinLoan.getInterestRate(), requestJoinLoan.getMonth());
        UsingProduct usingProduct = UsingProduct.create(requestJoinLoan.getUserId(), ProductType.NEGATIVE_LOANS,
                response.getBody(), requestJoinLoan.getName(), requestJoinLoan.getProductId());
        usingProduct.addLoanInuse(loanInUse);

        // DB에 저장
        usingProductRepository.save(usingProduct);

        // 성공 응답
        return usingProduct.getId();
    }


    /**
     * auth 서비스에 사용자 실명이 일치하는지 확인
     * @param userId 사용자 id
     * @param name 사용자 실명
     * @return Boolean type
     */
    private Boolean checkUserInfo(UUID userId, String name, UserDetailsImpl userDetails){
        return authClient2.findByUserIdAndName(userDetails.getUserId(),
                userDetails.getUsername(),
                userDetails.getRole(),
                userId,
                name);
    }
}
