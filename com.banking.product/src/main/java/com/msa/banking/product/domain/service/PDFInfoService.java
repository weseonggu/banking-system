package com.msa.banking.product.domain.service;

import com.msa.banking.product.application.dto.PDFCache;
import com.msa.banking.product.application.dto.ResponsePDFInfo;
import com.msa.banking.product.config.redis.RedisCacheKey;
import com.msa.banking.product.domain.model.PDFInfo;
import com.msa.banking.product.domain.repository.PDFInfoRepository;
import com.msa.banking.product.presentation.exception.custom.CustomDuplicateKeyException;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PDFInfoService {

    private final PDFInfoRepository pdfInfoRepository;
    
    // pdf 정보 저장
    public PDFInfo savePdfInfo(String originalName, String uploadFileName){
        try {
            PDFInfo pdf = PDFInfo.create(originalName, uploadFileName);
            return pdfInfoRepository.save(pdf);
        } catch (ConstraintViolationException e){
            throw new CustomDuplicateKeyException("다른 상품이 사용하는 파일 입니다.");
        }

    }
    
    // pdf 조회
    @Cacheable(cacheNames = RedisCacheKey.pdfCache, key = "args[0]")
    public PDFCache fingPdfInfo(Long pdfId) {
        return pdfInfoRepository.findById(pdfId).map(PDFCache :: of)
                .orElseThrow(() -> new IllegalArgumentException("없는 파일입니다."));
    }
}
