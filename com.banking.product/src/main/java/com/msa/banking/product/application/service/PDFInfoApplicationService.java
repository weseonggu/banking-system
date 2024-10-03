package com.msa.banking.product.application.service;

import com.msa.banking.product.application.dto.ResponsePDFInfo;
import com.msa.banking.product.domain.model.PDFInfo;
import com.msa.banking.product.domain.repository.PDFInfoRepository;
import com.msa.banking.product.lib.FileUtil;
import com.msa.banking.product.presentation.exception.custom.UnsupportedExtensionsException;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PDFInfoApplicationService {

    private final UploadService uploadService;
    private final PDFInfoRepository pdfInfoRepository;

    // pdf 저장 비지니스 로직
    public Long createPDFInfo(MultipartFile multipartFile){

        String originalName = multipartFile.getOriginalFilename();
        // 확장자 pdf인지 확인
        if (originalName == null || !originalName.toLowerCase().endsWith(".pdf")) {
            // 파일이 PDF이 아닐 경우
            throw new UnsupportedExtensionsException("PDF 만 등록해주세요");
        }
        // 파일명을 파일 명과 확장자를 불리
        String[] fileInfos = FileUtil.splitFileName(originalName);

        String uploadFileName = getRandomImageName() + ".pdf";
        // s3에 파일 저장
        uploadService.uploadImage(uploadFileName, multipartFile);

        PDFInfo pdf = PDFInfo.create(originalName, uploadFileName);
        pdfInfoRepository.save(pdf);
        return pdf.getId();
    }

    // S3 에 저장할 때 파일의 이름을 랜덤으로 생성해주는 메서드
    private String getRandomImageName() {
        return UUID.randomUUID().toString();
    }

    // 파일 조회
    public ResponsePDFInfo getPdf(Long pdfId) {
        PDFInfo pdf = pdfInfoRepository.findById(pdfId).orElseThrow(() -> new IllegalArgumentException("없는 파일입니다."));

        InputStream fileData = uploadService.getFile(pdf.getUploadFileName());

        return new ResponsePDFInfo(pdf.getId(), pdf.getFileName(), fileData);
    }
}
