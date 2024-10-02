package com.msa.banking.product.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.awt.*;
import java.time.LocalDateTime;
@Entity
@Table(name = "p_pdf_info")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder(access = AccessLevel.PRIVATE)
public class PDFInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    String fileName; // 파일명
    String uploadFileName; // S3에 저장된 파일의 이름

    public static PDFInfo create(String fileName, String uploadFileName){
        return PDFInfo.builder()
                .fileName(fileName)
                .uploadFileName(uploadFileName)
                .build();
    }
}
