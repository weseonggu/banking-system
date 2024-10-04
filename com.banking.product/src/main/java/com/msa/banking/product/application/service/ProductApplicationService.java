package com.msa.banking.product.application.service;

import com.msa.banking.product.application.dto.PDFCache;
import com.msa.banking.product.domain.model.CheckingDetail;
import com.msa.banking.product.domain.model.LoanDetail;
import com.msa.banking.product.domain.model.PDFInfo;
import com.msa.banking.product.domain.model.Product;
import com.msa.banking.product.domain.repository.CheckingDetailRepository;
import com.msa.banking.product.domain.repository.LoanDetailRepository;
import com.msa.banking.product.domain.service.PDFInfoService;
import com.msa.banking.product.domain.service.ProductService;
import com.msa.banking.product.presentation.exception.custom.CustomDuplicateKeyException;
import com.msa.banking.product.presentation.request.RequestCreateCheckingProduct;
import com.msa.banking.product.presentation.request.RequestCreateLoanProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProductApplicationService {

    private final PDFInfoService pdfInfoService;
    private final ProductService productService;
    private final CheckingDetailRepository checkingDetailRepository;
    private final LoanDetailRepository loanDetailRepository;

    // 입출금 상품 저장
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
        productService.saveCheckkingProduct(product, checkingDetail, pdf);


    }

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
        // 상품 저장
        product = productService.saveProduct(product);
        // 상품 디테일 저장
        productService.saveLoanProduct(product, loanDetail, pdf);
    }
}
