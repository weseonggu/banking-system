package com.msa.banking.product.application.service;

import com.msa.banking.commonbean.security.UserDetailsImpl;
import com.msa.banking.product.application.dto.*;
import com.msa.banking.product.config.redis.RedisCacheKey;
import com.msa.banking.product.domain.model.*;
import com.msa.banking.product.domain.repository.CheckingDetailRepository;
import com.msa.banking.product.domain.repository.LoanDetailRepository;
import com.msa.banking.product.domain.service.PDFInfoService;
import com.msa.banking.product.domain.service.ProductService;
import com.msa.banking.product.infrastructure.repository.CheckRedisState;
import com.msa.banking.product.infrastructure.repository.ProductRepository;
import com.msa.banking.product.presentation.exception.custom.ResourceNotFoundException;
import com.msa.banking.product.presentation.request.RequestCreateCheckingProduct;
import com.msa.banking.product.presentation.request.RequestCreateLoanProduct;
import com.msa.banking.product.presentation.request.RequestSearchProductDto;
import com.msa.banking.product.presentation.response.ResponseProductPage;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductApplicationService {

    private final PDFInfoService pdfInfoService;
    private final ProductService productService;
    private final CheckingDetailRepository checkingDetailRepository;
    private final LoanDetailRepository loanDetailRepository;
    private final AsyncService asyncService;
    private final CheckRedisState checkRedisState;
    private final ProductRepository productRepository;
    private final PlatformTransactionManager transactionManager;
    private final RedissonClient redisson;

    // 입출금 상품 등록
    @Transactional
    public void createCheckingProduct(RequestCreateCheckingProduct detail) {

        // PDF 파일이 있는 있는지 확인
        PDFCache pdfCache = pdfInfoService.fingPdfInfo(detail.getFileId());
        PDFInfo pdf = pdfCache.toEntity();
        if(!pdf.getUploadFileName().equals(detail.getFileName())){
            throw new IllegalArgumentException("없는 파일 입니다.");
        }
        // 이미 등록되어 있는 PDF인지 확인
        if(checkingDetailRepository.existsByPdfInfoId(pdf.getId())){
            throw new IllegalArgumentException("파일이 잘못되었습니다.");
        }

        // 입출금 상품 생성
        Product product = Product.create(detail.getName(), detail.getType(),
                 detail.getValid_from(), detail.getValid_to());
        // 입출금 상품 디테일 생성
        CheckingDetail checkingDetail = CheckingDetail.create(detail.getChcking_detail(), detail.getTerms_and_conditions(),
                 detail.getInterest_rate(), detail.getFees());
        // 상품 저장
        product = productService.saveProduct(product);
        // 상품 디테일 저장
        productService.saveCheckingProduct(product, checkingDetail, pdf);

        asyncService.afterCreateNewProductLoginc();
    }

    // 대출 상품 등록
    @Transactional
    public void createLoanProduct(RequestCreateLoanProduct detail) {
        // PDF 파일이 있는 있는지 확인
        PDFCache pdfCache = pdfInfoService.fingPdfInfo(detail.getFileId());
        PDFInfo pdf = pdfCache.toEntity();
        if(!pdf.getUploadFileName().equals(detail.getFileName())){
            throw new IllegalArgumentException("없는 파일 입니다.");
        }
        // 이미 등록되어 있는 PDF인지 확인
        if(loanDetailRepository.existsByPdfInfoId(pdf.getId())){
            throw new IllegalArgumentException("파일이 잘못되었습니다.");
        }
        // 대출 상품 생성
        Product product = Product.create(detail.getName(), detail.getType(),
                detail.getValid_from(), detail.getValid_to());
        // 대출 상품 디테일 생성
        LoanDetail loanDetail = LoanDetail.create(detail.getInterestRate(),
                detail.getMinAmount(), detail.getMaxAmount(),
                detail.getLoanTerm(), detail.getPreferentialInterestRates(),
                detail.getLoanDetail(), detail.getTerms_and_conditions());
        // 대출 상품 저장
        product = productService.saveProduct(product);
        // 대출 상품 디테일 저장
        productService.saveLoanProduct(product, loanDetail, pdf);

        asyncService.afterCreateNewProductLoginc();
    }


    // 상품 목록 조회
    public List<ResponseProductPage> findAllProduct(Pageable pageable, RequestSearchProductDto condition) {
        // 기본 한 페이지의 목록 개수 25
        int pageSize = 25;

        // 페이지 번호가 0보다 작은 경우 0으로 고정
        int pageNumber = Math.max(pageable.getPageNumber(), 0);

        // 정렬 로직
        Sort sort = pageable.getSort();

        // Sort가 비어있지 않다면, 각 정렬 기준을 확인
        if (sort.isSorted()) {
            for (Sort.Order order : sort) {
                if ("create_at".equals(order.getProperty())) {
                    sort = pageable.getSort();
                } else {
                    sort = Sort.by("create_at").descending();
                }
            }
        } else {
            sort = Sort.by("create_at").descending();
        }

        // 기본 Pageable 객체 생성
        Pageable newPageable = PageRequest.of(pageNumber, pageSize, sort);

        return productService.findAllProducts(newPageable, condition);


    }

    // 상품 디테일 조회
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = RedisCacheKey.ProductDetailCache, key = "#productId",
            unless = "#result == null", condition = "@checkRedisState.isRedisAvailable()" )
    public ProductResponseDto findProductDetail(UUID productId) {
        Product product = productService.findPrductInfo(productId);
        ProductDetailDto detailDto;

        if (product.getCheckingDetail() != null) {

            detailDto = CheckingDetailDto.of(product);

        } else if (product.getLoanDetail() != null) {

            detailDto = LoanDetailDto.of(product);

        } else {
            throw new ResourceNotFoundException("Product details not found");
        }

        return new ProductResponseDto(
                product.getId(),
                product.getName(),
                product.getType(),
                product.getValidFrom(),
                product.getValidTo(),
                product.getProductLike().getLikeCount(),
                product.getIsFinish(),
                detailDto  // LoanDetailDto 또는 CheckingDetailDto 반환
        );

    }

    /**
     * 상품 삭제
     * @param productId
     * @param userDetails
     */
    @Transactional
    public void deleteProduct(UUID productId, UserDetailsImpl userDetails) {
        productService.delete(productId, userDetails);
    }

//    /**
//     * 상품 좋아요
//     * @param userDetails
//     * @param productId
//     */
//    public void addLike(UserDetailsImpl userDetails, UUID productId) {
//        int maxRetryCount = 300;
//        int retryCount = 0;
//        boolean isSuccess = false;
//
//        while (retryCount < maxRetryCount && !isSuccess) {
//            TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
//            try {
//                Product product = productRepository.findByIdAndIsDeleteFalse(productId)
//                        .orElseThrow(() -> new IllegalArgumentException("없는 상품입니다."));
//
//                ProductLikeWho likeWho = ProductLikeWho.create(product.getProductLike(), userDetails.getUserId());
//                product.getProductLike().addLike(likeWho);
//
//                productRepository.save(product);
//                transactionManager.commit(status);
//                  // 성공 시 커밋
//                isSuccess = true;
//
//            } catch (ObjectOptimisticLockingFailureException e) {
//                retryCount++;
//                log.warn("낙관적 락 발생 - 재시도 횟수: {}", retryCount);
//                if (retryCount >= maxRetryCount) {
//                    transactionManager.rollback(status);
//                    throw new RuntimeException("최대 재시도 횟수를 초과하여 실패했습니다.");
//                }
//                try {
//                    Thread.sleep(100); // 짧은 대기 후 재시도
//                } catch (InterruptedException ie) {
//                    Thread.currentThread().interrupt();
//                }
//            } catch (Exception e) {
//                log.info("Exception 예외");
//                log.error(e.getMessage());
//
//                transactionManager.rollback(status);  // 그 외의 예외도 롤백
//                throw e;
//            }
//        }
//
//    }
    public void addLike(UserDetailsImpl userDetails, UUID productId) {
        String lockKey = "post:like:" + productId;
        RLock lock = redisson.getLock(lockKey);

        try {
            boolean isLocked = lock.tryLock(10, 5, TimeUnit.SECONDS); // 최대 대기 시간 10초, 락 유지 시간 5초
            if (isLocked) {
                Product product = productRepository.findByIdAndIsDeleteFalse(productId)
                        .orElseThrow(() -> new IllegalArgumentException("없는 상품입니다."));
                ProductLikeWho likeWho = ProductLikeWho.create(product.getProductLike(), userDetails.getUserId());
                product.getProductLike().addLike(likeWho);

                productRepository.save(product);
            } else {
                log.warn("좋아요 락 획득에 실패 했습니다.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("락 획득 대기 중 인터럽트 발생: " + e.getMessage());
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock(); // 락 해제
            }
        }
    }
    // TODO: 상품 좋아요 지우기
    public void deleteLike(UserDetailsImpl userDetails, UUID productId) {
        Product product = productRepository.findByIdAndIsDeleteFalse(productId).orElseThrow(() -> new IllegalArgumentException("없는 상품입니다."));
    }
}
