package com.msa.banking.product.application.dto;

import com.msa.banking.product.domain.model.PDFInfo;
import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class PDFCache implements Serializable {
    Long id;

    String fileName; // 파일명

    String uploadFileName; // S3에 저장된 파일의 이름


    public static PDFCache of(PDFInfo pdf){
        return PDFCache.builder()
                .id(pdf.getId())
                .fileName(pdf.getFileName())
                .uploadFileName(pdf.getUploadFileName())
                .build();
    }

    public PDFInfo toEntity() {
        return new PDFInfo(this.id, this.getFileName(), this.uploadFileName);
    }
}
