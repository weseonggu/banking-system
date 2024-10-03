package com.msa.banking.product.domain.model;

import com.msa.banking.common.base.AuditEntity;
import jakarta.persistence.*;
import lombok.*;

import java.awt.*;
import java.io.Serializable;
import java.time.LocalDateTime;
@Entity
@Table(name = "p_pdf_info")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder(access = AccessLevel.PRIVATE)
public class PDFInfo extends AuditEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "pdf_info_id")
    Long id;

    String fileName; // 파일명

    String uploadFileName; // S3에 저장된 파일의 이름

    //////////////////////////////////////////////////////////////////

    @OneToOne(mappedBy = "pdfInfo", fetch = FetchType.LAZY)
    private LoanDetail loanDetail;

    @OneToOne(mappedBy = "pdfInfo", fetch = FetchType.LAZY)
    private CheckingDetail checkingDetail;

    //////////////////////////////////////////////////////////////////

    public static PDFInfo create(String fileName, String uploadFileName){
        return PDFInfo.builder()
                .fileName(fileName)
                .uploadFileName(uploadFileName)
                .build();
    }
}
