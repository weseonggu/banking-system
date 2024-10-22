package com.msa.banking.product.application.service;

import com.msa.banking.common.account.dto.AccountRequestDto;
import com.msa.banking.common.account.dto.LoanDepositTransactionRequestDto;
import com.msa.banking.common.account.type.AccountType;
import com.msa.banking.common.account.type.TransactionType;
import com.msa.banking.common.base.UserRole;
import com.msa.banking.common.response.ErrorCode;
import com.msa.banking.commonbean.client.AuthClient;
import com.msa.banking.commonbean.exception.GlobalCustomException;
import com.msa.banking.commonbean.security.UserDetailsImpl;
import com.msa.banking.product.application.dto.*;
import com.msa.banking.product.domain.model.CheckingInUse;
import com.msa.banking.product.domain.model.LoanInUse;
import com.msa.banking.product.domain.model.Product;
import com.msa.banking.product.domain.model.UsingProduct;
import com.msa.banking.product.infrastructure.client.AccountClient;
import com.msa.banking.product.infrastructure.repository.ProductRepository;
import com.msa.banking.product.infrastructure.repository.UsingProductRepository;
import com.msa.banking.product.lib.LoanState;
import com.msa.banking.product.lib.ProductType;
import com.msa.banking.product.presentation.exception.custom.ResourceNotFoundException;
import com.msa.banking.product.presentation.exception.custom.TryAgainException;
import com.msa.banking.product.presentation.request.RequestJoinLoan;
import com.msa.banking.product.presentation.request.RequestUsingProductConditionDto;
import com.msa.banking.product.presentation.request.RequsetJoinChecking;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
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
import java.time.LocalDateTime;
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

    @Transactional
    public NewSubscriber joinChecking(RequsetJoinChecking requestDto, UserDetailsImpl userDetails) {
        try {
            // 중복 가입 및 기본 상품 유효성 체크
            Product product = validateAndRetrieveProduct(requestDto.getUserId(), requestDto.getProductId(), requestDto.getType(), userDetails, requestDto.getName());

            // CheckingInUse 엔티티 생성
            CheckingInUse checkingInUse = CheckingInUse.create(product.getCheckingDetail().getInterestRate(), requestDto.getFeeWaiver());

            // 계좌 생성 및 UsingProduct 생성
            NewSubscriber newJoiner = createAndSaveUsingProduct(requestDto.getUserId(), ProductType.CHECKING, requestDto.getName(),
                    requestDto.getProductId(), requestDto.getAccountPin(), checkingInUse, null);

            return newJoiner;
        }catch (FeignException e) {
            throw new IllegalArgumentException("계좌 생성 중 오류가 발생했습니다.");
        }

    }

    @Transactional
    public NewSubscriber joinLoan(RequestJoinLoan requestDto, UserDetailsImpl userDetails) {
        try {
            // 중복 가입 및 기본 상품 유효성 체크
            Product product = validateAndRetrieveProduct(requestDto.getUserId(), requestDto.getProductId(), requestDto.getType(), userDetails, requestDto.getName());
            // 대출 신청 금액이 가능한건지 채크.
            long max = product.getLoanDetail().getMaxAmount();
            long min = product.getLoanDetail().getMinAmount();
            if(!(min <= requestDto.getLoanAmount() && max >= requestDto.getLoanAmount())){
                throw new IllegalArgumentException(String.format("신청한 금액은 %d원에서 %d원 사이여야 합니다.", min, max));
            }

            // LoanInUse 엔티티 생성 이자는 상품에 정해진 대로
            LoanInUse loanInUse = LoanInUse.create(requestDto.getLoanAmount(), requestDto.getName(),
            product.getLoanDetail().getInterestRate(), requestDto.getMonth()) ;

            // 계좌 생성 및 UsingProduct 생성
            NewSubscriber newJoiner = createAndSaveUsingProduct(requestDto.getUserId(), ProductType.NEGATIVE_LOANS, requestDto.getName(),
                    requestDto.getProductId(), requestDto.getAccountPin(), null, loanInUse);

            return newJoiner;
        }catch (FeignException e) {
            throw new IllegalArgumentException("계좌 생성 중 오류가 발생했습니다.");
        }
    }

    // 중복 가입 및 상품 유효성 검사
    private Product validateAndRetrieveProduct(UUID userId, UUID productId, ProductType productType, UserDetailsImpl userDetails, String name) {
        try {

        // 중복 가입 여부 확인
        if(usingProductRepository.existsByUserIdAndProductIdAndIsUsing(userId, productId, true)){
            throw new IllegalArgumentException("상품 중복 가입입니다.");
        }

        // 상품 유효성 확인
        Product product = productRepository.findByIdWhereIsDeleted(productId, false, productType, LocalDateTime.now())
                .orElseThrow(() -> new IllegalArgumentException("없거나 더이상 가입이 불가능한 상품입니다."));

        if(!product.getType().equals(productType)){
            throw new IllegalArgumentException("입출금 상품 가입입니다. 다른 상품은 안됩니다.");
        }

        // 사용자 권한 확인 (일반 사용자는 본인만 가입 가능)
        if(userDetails.getRole().equals(UserRole.CUSTOMER.getAuthority())) {
            if(!userDetails.getUserId().equals(userId)){
                throw new IllegalArgumentException("타인의 아이디로 가입 할 수 없습니다.");
            }
        }

        // 사용자 정보 일치 확인
        if(!checkUserInfo(userId, name)) {
            throw new IllegalArgumentException("사용자 정보가 일치 하지 않습니다.");
        }

        return product;

        }catch (FeignException e) {
            throw new IllegalArgumentException("계좌 생성 중 오류가 발생했습니다.");
        }
    }

    // UsingProduct 및 관련 엔티티 생성 및 저장
    private NewSubscriber createAndSaveUsingProduct(UUID userId, ProductType productType, String name, UUID productId,
                                                    String accountPin, CheckingInUse checkingInUse, LoanInUse loanInUse) throws FeignException {

        // 계좌 생성 요청
        ResponseEntity<UUID> response = addAccount(name, accountPin, productType);

        // UsingProduct 엔티티 생성
        UsingProduct usingProduct = UsingProduct.create(userId, productType, response.getBody(), name, productId);

        // 관련된 상품 엔티티 연결
        if (checkingInUse != null) {
            usingProduct.addChckingInuse(checkingInUse);
        }
        if (loanInUse != null) {
            usingProduct.addLoanInuse(loanInUse);
        }

        // DB에 저장
        usingProductRepository.save(usingProduct);

        // 성공 응답
        return new NewSubscriber(name, response.getBody(), userId, usingProduct.getId());
    }

    // 계좌 생성 요청  서킷 브레이커적용하기
    @CircuitBreaker(name = "createAccountService", fallbackMethod = "joinFallbackMethod")
    private ResponseEntity<UUID> addAccount(String name, String accountPin, ProductType accountType) throws FeignException{
        AccountRequestDto requestDto =  null;
        switch (accountType){
            case CHECKING:
                requestDto = new AccountRequestDto(name, AccountType.CHECKING, accountPin, accountPin);
                break;

            case NEGATIVE_LOANS:
                requestDto = new AccountRequestDto(name, AccountType.LOAN, accountPin, accountPin);
        }
        return accountClient.addAccount(requestDto);


    }

    /**
     * auth 서비스에 사용자 실명이 일치하는지 확인
     * @param userId 사용자 id
     * @param name 사용자 실명
     * @return Boolean type
     */
    @CircuitBreaker(name = "uerCheckService", fallbackMethod = "userCheckFallbackMethod")
    private Boolean checkUserInfo(UUID userId, String name){
        return authClient.findByUserIdAndName(userId, name);
    }

    public NewSubscriber joinFallbackMethod(Exception e){



        throw new TryAgainException("서비스에 문제가 생겼습니다. 나중에 다시 시도해주세요");
    }

    public NewSubscriber userCheckFallbackMethod(Exception e){



        throw new TryAgainException("서비스에 문제가 생겼습니다. 나중에 다시 시도해주세요");
    }

////////////////////////////////////////////////////// 여기 까지 상품 가입 관련 서비스 로직 /////////////////////////////////////////////////////////////


    /**
     * 가입한 상품 조회 [직원은 실명 검색이 가능, 일반 사용자는 자기만 조회 가능]
     * @param page 조회 페이지 정보
     * @param condition 조회 조건 [userid, name(실명)]
     */
    @Transactional(readOnly = true)
    public List<UsingProductPage> fingUsingProductPage(Pageable page, RequestUsingProductConditionDto condition, UserDetailsImpl userDetails) {
        // 일반 사용자일 경우 자기만 조회가능, 직원일 경우 실명을 가지고 조회 가능
        if(userDetails.getRole().equals(UserRole.CUSTOMER.getAuthority())){
            if(!userDetails.getUserId().equals(condition.getUserid())){
                throw new AccessDeniedException("타인의 정보는 볼 수 없습니다.");
            }
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
    public boolean changeLoanSate(UUID id, UserDetailsImpl userDetails, boolean choice) {
        // 가입신청한 상품 찾기
        UsingProduct usingProduct =usingProductRepository.findByIdEntityGraph(id)
                .orElseThrow(() -> new IllegalArgumentException("데이터가 없습니다."));

        if(usingProduct.getLoanInUse()==null){
            throw new IllegalArgumentException("데이터가 없거나 신청상태가 아닙니다.");
        }

        if(usingProduct.getLoanInUse().getStatus().equals(LoanState.REFUASAL)){
            throw new IllegalArgumentException("거부된 대출입니다.");
        }

        // 데이터가 신청 상태인지 확인
        if(usingProduct.getLoanInUse().getStatus().equals(LoanState.APPLY)){
            if(choice){
                // 대출 실행 전으로 변경
                usingProduct.getLoanInUse().approvalLoan(userDetails.getUsername());
                usingProduct.getLoanInUse().reviewerUp(userDetails.getUserId());
                usingProductRepository.save(usingProduct);
                return true;
            }else{
                // 대출 거부로 변경
                usingProduct.getLoanInUse().refusalLoan(userDetails.getUsername());
                usingProduct.getLoanInUse().reviewerUp(userDetails.getUserId());
                usingProductRepository.save(usingProduct);
                usingProduct.changeIsUsing(false);
                return false;
            }
        }else{
            throw new IllegalArgumentException("이미 대출승인 여부가 정해진 대출입니다.");
        }
    }

    /**
     * 대출 실행
     * @param id 실행 할 대출 id
     */
    @Transactional
    public void changeLoanSateToRun(UUID id, UserDetailsImpl userDetails, String accountNum) {
        // 실행할 데이터 검색
        UsingProduct usingProduct =usingProductRepository.findByAccountIdEntityGraph(id)
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
        try {
            // 계좌 증액 요청
            LoanDepositTransactionRequestDto dto = new LoanDepositTransactionRequestDto(
                    accountNum,
                    TransactionType.LOAN_DEPOSIT,
                    BigDecimal.valueOf(usingProduct.getLoanInUse().getLoanAmount()),
                    usingProduct.getName()+"님 대출금 입금"
            );
            accountClient.updateAccount(usingProduct.getAccountId(), dto);
        }catch (FeignException.BadRequest e){
            throw new IllegalArgumentException(e.getMessage());
        }


        // 대출 실행 전으로 변경
        usingProduct.getLoanInUse().runLoan(userDetails.getUsername());

        usingProductRepository.save(usingProduct);


    }

    /**
     * 사용중인 상품 조회
     */
    public UsingProductDetailDto findUsingProductDetail(UUID id, UserDetailsImpl userDetails) {

        UsingProduct usingProduct = usingProductRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("상품을 가입하지 않았습니다."));

        // 일반 사용자는 타인의 자료를 조회 할 수 없습니다.
        if (userDetails.getRole().equals(UserRole.CUSTOMER.getAuthority())) {
            if (!userDetails.getUserId().equals(usingProduct.getUserId())) {
                throw new AccessDeniedException("타인의 정보는 볼 수 없습니다.");
            }
        }
        UsingProductDetailDto detailDto;
        if (usingProduct.getCheckingInUse() != null) {

            detailDto = CheckingInUseDetailDto.of(usingProduct);

        } else if (usingProduct.getLoanInUse() != null) {

            detailDto = LoanInuseDetailDto.of(usingProduct);

        } else {
            throw new ResourceNotFoundException("Product details not found");
        }
        return detailDto;
    }
/////////////////////////////////////////////////////////// 상품 해지 로직 /////////////////////////////////////////////////////////
    /**
     * 사용 중인 상품 해지
     * @param usingProductId 사용중인 상품 id
     * @param userDetails 로그인 정보
     */
    @Transactional
    public boolean terminationProduct(UUID usingProductId, UserDetailsImpl userDetails) {
        UsingProduct usingProduct = usingProductRepository.findByIdJoinBothTable(usingProductId).orElseThrow(() -> new IllegalArgumentException("가입한 상품이 없습니다."));

        // 요청자가 일반 사용자일 때 보인 상품인지 확인
        if(userDetails.getRole().equals(UserRole.CUSTOMER.getAuthority())){
            if(!userDetails.getUserId().equals(usingProduct.getUserId())){
                throw new IllegalArgumentException("타인의 상품을 해지 할 수 없습니다.");
            }
        }
        boolean result = false;
        switch (usingProduct.getType()){
            case CHECKING :
                if(terminateChecking(usingProduct.getAccountId())){
                    // 입출금 상품 상태 변경
                    usingProduct.changeIsUsing(false);
                    usingProduct.delete(userDetails.getUsername());
                    usingProduct.getCheckingInUse().delete(userDetails.getUsername());
                    usingProductRepository.save(usingProduct);
                    result = true;
                }else {
                    result = false;
                }
                break;
            case NEGATIVE_LOANS:
                if(terminateNativeLoan(usingProduct.getAccountId(), usingProduct.getLoanInUse().getLoanAmount())){
                    // 대출 상품 상태 변경
                    usingProduct.changeIsUsing(false);
                    usingProduct.getLoanInUse().cancleLoan();
                    usingProduct.delete(userDetails.getUsername());
                    usingProduct.getLoanInUse().delete(userDetails.getUsername());
                    usingProductRepository.save(usingProduct);
                    result = true;
                }else {
                    result= false;
                }
                break;

        }

        return result;
    }



    /**
     * 입출금 상품 해지
     */
    @CircuitBreaker(name = "terminateService", fallbackMethod = "terminateFallbackMethod")
    private Boolean terminateChecking(UUID accountId) {
        try {
            return accountClient.deleteAccount(accountId);
        }catch (FeignException e){
            if (e.status() == 400) {
                return false;
            } else if (e.status() == 404) {
                return false;
            } else {
                // 그 외의 예외 처리
                throw e;
            }
        }


    }

    /**
     * 대출 상품 해지
     * @param ammount
     */
    @CircuitBreaker(name = "terminateService", fallbackMethod = "terminateFallbackMethod")
    private Boolean terminateNativeLoan(UUID accountId, Long ammount) {
        try {
          BigDecimal money = BigDecimal.valueOf(ammount);
          return accountClient.deleteLoanAccount(accountId, money);
        }catch (FeignException e){
            if (e.status() == 400) {
                return false;
            } else if (e.status() == 404) {
                return false;
            } else {
                // 그 외의 예외 처리
                throw e;
            }
        }

    }

    public Boolean terminateFallbackMethod(Exception e){
        return false;
    }

/////////////////////////////////////////////////////////// 상품 해지 로직 /////////////////////////////////////////////////////////
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
