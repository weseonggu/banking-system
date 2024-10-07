package com.msa.banking.product.application.dto;

import com.msa.banking.product.domain.model.PDFInfo;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class PDFInfoDto {
    private Long id;
    private String fileName;

    public static PDFInfoDto of(PDFInfo pdf){
        return PDFInfoDto.builder()
                .id(pdf.getId())
                .fileName(pdf.getFileName())
                .build();
    }
}
