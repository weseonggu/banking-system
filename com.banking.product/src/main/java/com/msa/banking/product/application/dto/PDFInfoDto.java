package com.msa.banking.product.application.dto;

import com.msa.banking.product.domain.model.PDFInfo;
import lombok.*;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class PDFInfoDto implements Serializable {
    private Long id;
    private String fileName;

    public static PDFInfoDto of(PDFInfo pdf){
        return PDFInfoDto.builder()
                .id(pdf.getId())
                .fileName(pdf.getFileName())
                .build();
    }
}
