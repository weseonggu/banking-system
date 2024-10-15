package com.msa.banking.product.application.dto;

import com.msa.banking.product.domain.model.CheckingDetail;
import com.msa.banking.product.domain.model.Product;
import lombok.*;

import java.math.BigDecimal;
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class CheckingDetailDto extends ProductDetailDto {
    private String checkingDetail;
    private String termsAndConditions;
    private BigDecimal interestRate;
    private int fees;
    private PDFInfoDto pdfInfo;

    public static CheckingDetailDto of (Product product) {
        CheckingDetail checkingDetail = product.getCheckingDetail();

        return CheckingDetailDto.builder()
                .checkingDetail(checkingDetail.getCheckingDetail())
                .termsAndConditions(checkingDetail.getTermsAndConditions())
                .interestRate(checkingDetail.getInterestRate())
                .fees(checkingDetail.getFees())
                .pdfInfo(PDFInfoDto.of(checkingDetail.getPdfInfo()))
                .build();


    }

}
