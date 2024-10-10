package com.msa.banking.product.application.service;

import com.msa.banking.common.account.dto.AccountRequestDto;
import com.msa.banking.common.account.type.AccountStatus;
import com.msa.banking.common.account.type.AccountType;
import com.msa.banking.common.base.UserRole;
import com.msa.banking.common.response.ErrorCode;
import com.msa.banking.commonbean.exception.GlobalCustomException;
import com.msa.banking.commonbean.security.UserDetailsImpl;
import com.msa.banking.product.application.dto.UsingProductPage;
import com.msa.banking.product.application.dto.UsingProductResponseDto;
import com.msa.banking.product.domain.model.CheckingInUse;
import com.msa.banking.product.domain.model.LoanInUse;
import com.msa.banking.product.domain.model.UsingProduct;
import com.msa.banking.product.infrastructure.client.AccountClient;
import com.msa.banking.product.infrastructure.repository.ProductRepository;
import com.msa.banking.product.infrastructure.repository.UsingProductRepository;
import com.msa.banking.product.lib.ProductType;
import com.msa.banking.product.presentation.request.RequestJoinLoan;
import com.msa.banking.product.presentation.request.RequestUsingProductConditionDto;
import com.msa.banking.product.presentation.request.RequsetJoinChecking;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsingProductService {

    private final AccountClient accountClient;
    private final UsingProductRepository usingProductRepository;
    private final ProductRepository productRepository;

    /**
     * 입 출금 상품 가입
     * @param requsetJoinChecking
     * @param userDetails
     * @return
     */
    @Transactional
    public UUID joinChecking(RequsetJoinChecking requsetJoinChecking, UserDetailsImpl userDetails) {

        // 상품이 있는지 확인
        if(!productRepository.existsByIdWhereIsDeleted(requsetJoinChecking.getProductId(), false, requsetJoinChecking.getType())){
            throw new IllegalArgumentException("없거나 거이상 가입이 불가능한 상품입니다.");
        }

        // 요청자 확인 직원일 경우 가능, 일반 사용자일경우 userDetails랑 dto의 id가 같은지 확인
        if(userDetails.getRole().equals(UserRole.CUSTOMER.getAuthority())){
            if(!userDetails.getUserId().equals(requsetJoinChecking.getUserId())){
                throw new IllegalArgumentException("타인의 아이디로 가입 할 수 없습니다.");
            }
        }
        // TODO: 사용자가 있는지 확인 요청  userId랑 name으로
        // 계좌 생성 요청 -> 응답으로 계좌 id(UUID)를 반환 예외처리
        AccountRequestDto requestDto = new AccountRequestDto(requsetJoinChecking.getName(),
                AccountStatus.ACTIVE,
                AccountType.CHECKING,
                requsetJoinChecking.getAccountPin());

        ResponseEntity<UUID> response = accountClient.addAccount(requestDto);


        // 엔티티 생성 UsingProduct, CeckingInUse 생성
        CheckingInUse checkingInUse = CheckingInUse.create(requsetJoinChecking.getInterestRate(), requsetJoinChecking.getFeeWaiver());
        UsingProduct usingProduct = UsingProduct.create(requsetJoinChecking.getUserId(), ProductType.CHECKING,
                response.getBody(), requsetJoinChecking.getName(), requsetJoinChecking.getProductId());
        usingProduct.addChckingInuse(checkingInUse);

        // DB에저장
        usingProductRepository.save(usingProduct);
        // 성공 응답
        return response.getBody();
    }

    /**
     * 대출 상품 가입
     * @param requsetJoinLoan
     * @param userDetails
     * @return
     */
    @Transactional
    public UUID joinLoan(RequestJoinLoan requsetJoinLoan, UserDetailsImpl userDetails) {
        // 상품이 있는지 확인
        if(!productRepository.existsByIdWhereIsDeleted(requsetJoinLoan.getProductId(), false, requsetJoinLoan.getType())){
            throw new IllegalArgumentException("없거나 거이상 가입이 불가는한 상품입니다.");
        }

        // 요청자 확인 직원일 경우 가능, 일반 사용자일경우 userDetails랑 dto의 id가 같은지 확인
        if(userDetails.getRole().equals(UserRole.CUSTOMER.getAuthority())){
            if(!userDetails.getUserId().equals(requsetJoinLoan.getUserId())){
                throw new IllegalArgumentException("타인의 아이디로 가입 할 수 없습니다.");
            }
        }

        // TODO: 사용자가 있는지 확인 요청  userId랑 name으로
        // 계좌 생성 요청 -> 응답으로 계좌 id(UUID)를 반환 예외처리
        AccountRequestDto requestDto = new AccountRequestDto(requsetJoinLoan.getName(),
                AccountStatus.ACTIVE,
                AccountType.CHECKING,
                requsetJoinLoan.getAccountPin());

        ResponseEntity<UUID> response = accountClient.addAccount(requestDto);


        // 엔티티 생성 UsingProduct, LoanInUse 생성
        LoanInUse loanInUse = LoanInUse.create(requsetJoinLoan.getLoanAmount(), requsetJoinLoan.getName(), requsetJoinLoan.getInterestRate(), requsetJoinLoan.getMonth());
        UsingProduct usingProduct = UsingProduct.create(requsetJoinLoan.getUserId(), ProductType.NEGATIVE_LOANS,
                response.getBody(), requsetJoinLoan.getName(), requsetJoinLoan.getProductId());
        usingProduct.addLoanInuse(loanInUse);

        // DB에저장
        usingProductRepository.save(usingProduct);
        // 성공 응답
        return response.getBody();
    }

    /**
     * 가입한 상품 조회 [직원은 실명 검색이 가능, 일반 사용자는 자기만 조회 가능]
     * @param page 조회 페이지 정보
     * @param condition 조회 조건 [userid, name(실명)]
     */
    @Transactional(readOnly = true)
    public List<UsingProductPage> fingUsingProductPage(Pageable page, RequestUsingProductConditionDto condition, UserDetailsImpl userDetails) {
        // 일반 사용자일 경우 자기만 조회가능, 직원일 경우 실명을 가지고 조회 가능
        if(userDetails.getRole().equals(UserRole.CUSTOMER.getAuthority())){
            condition.setName(null);
        }
        // 검색 조건이 다 null 인경우
        if(condition.getUserid()==null && condition.getName()==null){
            throw new IllegalArgumentException("조회 조건이 이상합니다.");
        }
        // UsingProduct를 가지고 검색을 하는데 정렬 기준은 create_at만 사용가능
        int pageSize = 10;
        // 페이지 번호가 0보다 작은 경우 0으로 고정
        int pageNumber = Math.max(page.getPageNumber(), 0);
        // 정렬 로직
        Sort sort = Sort.by("create_at").descending();
        // 기본 Pageable 객체 생성
        Pageable newPageable = PageRequest.of(pageNumber, pageSize, sort);
        // DB 조회
        return usingProductRepository.findAllUsingProductPages(newPageable, condition);
    }

    public UsingProductResponseDto findByAccountId(UUID accountId, UUID userId, String userRole){

        UsingProduct usingProduct = usingProductRepository.findByAccountIdAndIsDeleteFalse(accountId).orElseThrow(
                () -> new IllegalArgumentException("잘못된 AccountId 입니다."));

        if (userRole.equals("CUSTOMER") && !userId.equals(usingProduct.getUserId())) {
            throw new GlobalCustomException(ErrorCode.USER_FORBIDDEN);
        }

        return UsingProductResponseDto.toDTO(usingProduct);
    }
}
