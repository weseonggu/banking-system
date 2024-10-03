package com.msa.banking.product.domain.service;

import com.msa.banking.product.application.dto.ResponsePDFInfo;
import com.msa.banking.product.config.redis.RedisCacheKey;
import com.msa.banking.product.domain.model.PDFInfo;
import com.msa.banking.product.domain.repository.PDFInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PDFInfoService {

    private final PDFInfoRepository pdfInfoRepository;
    
    // pdf 정보 저장
    public PDFInfo savePdfInfo(String originalName, String uploadFileName){
        PDFInfo pdf = PDFInfo.create(originalName, uploadFileName);
        return pdfInfoRepository.save(pdf);
    }
    
    // pdf 조회
    @Cacheable(cacheNames = RedisCacheKey.pdfCache, key = "args[0]")
    public PDFInfo fingPdfInfo(Long pdfId) {
        return pdfInfoRepository.findById(pdfId)
                .orElseThrow(() -> new IllegalArgumentException("없는 파일입니다."));
    }
}
