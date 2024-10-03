package com.msa.banking.product.application.service;

import com.msa.banking.product.domain.model.CheckingDetail;
import com.msa.banking.product.domain.model.PDFInfo;
import com.msa.banking.product.domain.model.Product;
import com.msa.banking.product.domain.service.PDFInfoService;
import com.msa.banking.product.domain.service.ProductService;
import com.msa.banking.product.presentation.exception.custom.CustomDuplicateKeyException;
import com.msa.banking.product.presentation.request.RequestCreateCheckingProduct;
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

    // 입출금 상품 저장
    @Transactional
    public void createCheckingProduct(RequestCreateCheckingProduct detail) {
        try {
            // PDF 파일이 있는 있는지 확인
            PDFInfo pdf = pdfInfoService.fingPdfInfo(detail.getFileId());
            if(!pdf.getUploadFileName().equals(detail.getFileName())){
                throw new IllegalArgumentException("없는 파일 입니다.");
            }
            // 입출금 상품 생성
            Product product = Product.create(detail.getName(), detail.getType(),
                    detail.getValid_from(), detail.getValid_to());
            // 입출금 상품 디테일 생성 -> 비동기 처리
            CheckingDetail checkingDetail = CheckingDetail.create(detail.getChcking_detail(), detail.getTerms_and_conditions(),
                    detail.getInterest_rate(), detail.getFees());
            // 상품 저장
            product = productService.saveProduct(product);
            // 상품 디테일 저장
            productService.saveCheckkingProduct(product, checkingDetail, pdf);
        } catch (DataIntegrityViolationException e) {
            // 중복 키 예외 처리
            throw new CustomDuplicateKeyException("이미 존재하는 키입니다.");
        }catch (Exception e){
            throw new CustomDuplicateKeyException("이미 존재하는 키입니다.");
        }

    }
}
