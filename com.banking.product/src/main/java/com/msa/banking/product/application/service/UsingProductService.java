package com.msa.banking.product.application.service;

import com.msa.banking.common.account.dto.AccountRequestDto;
import com.msa.banking.common.account.dto.SingleTransactionRequestDto;
import com.msa.banking.common.account.type.AccountStatus;
import com.msa.banking.common.account.type.AccountType;
import com.msa.banking.common.account.type.TransactionType;
import com.msa.banking.common.base.UserRole;
import com.msa.banking.common.response.ErrorCode;
import com.msa.banking.commonbean.client.AuthClient;
import com.msa.banking.commonbean.exception.GlobalCustomException;
import com.msa.banking.commonbean.security.UserDetailsImpl;
import com.msa.banking.product.application.dto.UsingProductPage;
import com.msa.banking.product.application.dto.UsingProductResponseDto;
import com.msa.banking.product.domain.model.CheckingInUse;
import com.msa.banking.product.domain.model.LoanInUse;
import com.msa.banking.product.domain.model.Product;
import com.msa.banking.product.domain.model.UsingProduct;
import com.msa.banking.product.infrastructure.client.AccountClient;
import com.msa.banking.product.infrastructure.repository.ProductRepository;
import com.msa.banking.product.infrastructure.repository.UsingProductRepository;
import com.msa.banking.product.lib.LoanState;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsingProductService {

    private final AccountClient accountClient;
    private final UsingProductRepository usingProductRepository;
    private final ProductRepository productRepository;
    private final AuthClient authClient;

    /**
     * 입 출금 상품 가입
     * @param requsetJoinChecking
     * @param userDetails
     * @return
     */
    @Transactional
    public UUID joinChecking(RequsetJoinChecking requsetJoinChecking, UserDetailsImpl userDetails) {

        // 해지가 아닌 상태에서 중복 가입 불가능
        if(usingProductRepository.existsByUserIdAndProductIdAndIsUsing(requsetJoinChecking.getUserId(), requsetJoinChecking.getProductId(), true)){
            throw new IllegalArgumentException("상품 중복 가입입니다.");
        }

        // 상품이 있는지 확인
        Product product = productRepository.findByIdWhereIsDeleted(requsetJoinChecking.getProductId(), false, requsetJoinChecking.getType())
                .orElseThrow(() -> new IllegalArgumentException("없거나 더이상 가입이 불가능한 상품입니다."));
        // 가입할려는 강비의 타입이 같은지 확인
        if(!product.getType().equals(requsetJoinChecking.getType())){
            throw new IllegalArgumentException("입출금 상품 가입입니다. 다른 상품은 안됩니다.");
        }

        // 요청자 확인 직원일 경우 가능, 일반 사용자일경우 userDetails랑 dto의 id가 같은지 확인
        if(userDetails.getRole().equals(UserRole.CUSTOMER.getAuthority())){
            if(!userDetails.getUserId().equals(requsetJoinChecking.getUserId())){
                throw new IllegalArgumentException("타인의 아이디로 가입 할 수 없습니다.");
            }
        }
        // 사용자가 있는지 확인 요청  userId랑 name으로
        if(!checkUserInfo(requsetJoinChecking.getUserId(), requsetJoinChecking.getName())){
            throw new IllegalArgumentException("사용자 정보가 일치 하지 않습니다.");
        }
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

        // 해지가 아닌 상태에서 중복 가입 불가능
        if(usingProductRepository.existsByUserIdAndProductIdAndIsUsing(requsetJoinLoan.getUserId(), requsetJoinLoan.getProductId(), true)){
            throw new IllegalArgumentException("상품 중복 가입입니다.");
        }

        // 상품이 있는지 확인
        Product product = productRepository.findByIdWhereIsDeleted(requsetJoinLoan.getProductId(), false, requsetJoinLoan.getType())
                .orElseThrow(() -> new IllegalArgumentException("없거나 거이상 가입이 불가능한 상품입니다."));
        if(!product.getType().equals(requsetJoinLoan.getType())){
            throw new IllegalArgumentException("입출금 상품 가입입니다. 다른 상품은 안됩니다.");
        }

        // 요청자 확인 직원일 경우 가능, 일반 사용자일경우 userDetails랑 dto의 id가 같은지 확인
        if(userDetails.getRole().equals(UserRole.CUSTOMER.getAuthority())){
            if(!userDetails.getUserId().equals(requsetJoinLoan.getUserId())){
                throw new IllegalArgumentException("타인의 아이디로 가입 할 수 없습니다.");
            }
        }

        // 사용자가 있는지 확인 요청  userId랑 name으로
        if(!checkUserInfo(requsetJoinLoan.getUserId(), requsetJoinLoan.getName())){
            throw new IllegalArgumentException("사용자 정보가 일치 하지 않습니다.");
        }
        // 계좌 생성 요청 -> 응답으로 계좌 id(UUID)를 반환 예외처리
        AccountRequestDto requestDto = new AccountRequestDto(requsetJoinLoan.getName(),
                AccountStatus.ACTIVE,
                AccountType.CHECKING,
                requsetJoinLoan.getAccountPin());

        ResponseEntity<UUID> response = accountClient.addAccount(requestDto);


        // 엔티티 생성 UsingProduct, LoanInUse 생성
        LoanInUse loanInUse = LoanInUse.create(requsetJoinLoan.getLoanAmount(), requsetJoinLoan.getName(),
                requsetJoinLoan.getInterestRate(), requsetJoinLoan.getMonth());
        UsingProduct usingProduct = UsingProduct.create(requsetJoinLoan.getUserId(), ProductType.NEGATIVE_LOANS,
                response.getBody(), requsetJoinLoan.getName(), requsetJoinLoan.getProductId());
        usingProduct.addLoanInuse(loanInUse);

        // DB에저장
        usingProductRepository.save(usingProduct);
        // 성공 응답
        return response.getBody();
    }

    /**
     * auth 서비스에 사용자 실명이 일치하는지 확인
     * @param userId 사용자 id
     * @param name 사용자 실명
     * @return Boolean type
     */
    private Boolean checkUserInfo(UUID userId, String name){
        return authClient.findByUserIdAndName(userId, name);
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

    /**
     * 대출 승인 로직
     * @param id 가입한 상품 id
     * @param userDetails 인가 정보
     */
    @Transactional
    public void changeLoanSate(UUID id, UserDetailsImpl userDetails) {
        // 가입신청한 상품 찾기
        UsingProduct usingProduct =usingProductRepository.findByIdEntityGraph(id)
                .orElseThrow(() -> new IllegalArgumentException("데이터가 없습니다."));

        if(usingProduct.getLoanInUse()==null){
            throw new IllegalArgumentException("데이터가 없거나 신청상태가 아닙니다.");
        }

        // 데이터가 신청 상태인지 확인
        if(!usingProduct.getLoanInUse().getStatus().equals(LoanState.APPLY)){
            throw new IllegalArgumentException("이미 승인한 대출입니다.");
        }
        // 대출 실행 전으로 변경
        usingProduct.getLoanInUse().approvalLoan(userDetails.getUsername());

        usingProductRepository.save(usingProduct);
    }

    /**
     * 대출 실행
     * @param id 실행 할 대출 id
     * @param userDetails
     */
    @Transactional
    public void changeLoanSateToRun(UUID id, UserDetailsImpl userDetails) {
        // 실행할 데이터 검색
        UsingProduct usingProduct =usingProductRepository.findByIdEntityGraph(id)
                .orElseThrow(() -> new IllegalArgumentException("데이터가 없습니다."));

        // 요청 이 본인 인지 확인
        if(!userDetails.getUserId().equals(usingProduct.getUserId())){
            throw new AccessDeniedException("본인 만 요청 가능합니다.");
        }

        if(usingProduct.getLoanInUse()==null){
            throw new IllegalArgumentException("데이터가 없거나 실행 전 상태가 아닙니다.");
        }
        // 데이터가 실행전 상태인지 확인
        if(!usingProduct.getLoanInUse().getStatus().equals(LoanState.BEFOREEXECUTION)){
            throw new IllegalArgumentException("대출 실행 가능한 상태가 아닙니다.");
        }
        // 계좌 증액 요청
        SingleTransactionRequestDto dto = new SingleTransactionRequestDto(
                TransactionType.DEPOSIT,
                BigDecimal.valueOf(usingProduct.getLoanInUse().getLoanAmount()),
                usingProduct.getName()+"님 대출금 입금",
                ""
        );
        accountClient.updateAccount(usingProduct.getAccountId(), dto);

        // 대출 실행 전으로 변경
        usingProduct.getLoanInUse().runLoan(userDetails.getUsername());

        usingProductRepository.save(usingProduct);


    }



/////////////////////////////////////////////   다른 마이크로 서비스    ///////////////////////////////////////////////////////////

    public UsingProductResponseDto findByAccountId(UUID accountId, UUID userId, String userRole){

        UsingProduct usingProduct = usingProductRepository.findByAccountIdAndIsDeleteFalse(accountId).orElseThrow(
                () -> new IllegalArgumentException("잘못된 AccountId 입니다."));

        if (userRole.equals("CUSTOMER") && !userId.equals(usingProduct.getUserId())) {
            throw new GlobalCustomException(ErrorCode.USER_FORBIDDEN);
        }

        return UsingProductResponseDto.toDTO(usingProduct);
    }



}
