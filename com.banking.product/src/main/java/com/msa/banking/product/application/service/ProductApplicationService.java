package com.msa.banking.product.application.service;

import com.msa.banking.product.application.dto.PDFCache;
import com.msa.banking.product.application.dto.ResponseProductPage;
import com.msa.banking.product.domain.model.CheckingDetail;
import com.msa.banking.product.domain.model.LoanDetail;
import com.msa.banking.product.domain.model.PDFInfo;
import com.msa.banking.product.domain.model.Product;
import com.msa.banking.product.domain.repository.CheckingDetailRepository;
import com.msa.banking.product.domain.repository.LoanDetailRepository;
import com.msa.banking.product.domain.service.PDFInfoService;
import com.msa.banking.product.domain.service.ProductService;
import com.msa.banking.product.presentation.request.RequestCreateCheckingProduct;
import com.msa.banking.product.presentation.request.RequestCreateLoanProduct;
import com.msa.banking.product.presentation.request.RequestSearchProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductApplicationService {

    private final PDFInfoService pdfInfoService;
    private final ProductService productService;
    private final CheckingDetailRepository checkingDetailRepository;
    private final LoanDetailRepository loanDetailRepository;
    private final AsyncService asyncService;

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
}
