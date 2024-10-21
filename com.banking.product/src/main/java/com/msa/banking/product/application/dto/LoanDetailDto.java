package com.msa.banking.product.application.dto;

import com.msa.banking.product.domain.model.LoanDetail;
import com.msa.banking.product.domain.model.Product;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class LoanDetailDto extends ProductDetailDto implements Serializable {
    private BigDecimal interestRate;
    private Long minAmount;
    private Long maxAmount;
    private int loanTerm;
    private String loanDetail;
    private String termsAndConditions;
    private PDFInfoDto pdfInfo;

    public static LoanDetailDto of(Product product) {
        LoanDetail loanDetail = product.getLoanDetail();
        return LoanDetailDto.builder()
                .interestRate(loanDetail.getInterestRate())
                .minAmount(loanDetail.getMinAmount())
                .maxAmount(loanDetail.getMaxAmount())
                .loanTerm(loanDetail.getLoanTerm())
                .loanDetail(loanDetail.getLoanDetail())
                .termsAndConditions(loanDetail.getTermsAndConditions())
                .pdfInfo(PDFInfoDto.of(loanDetail.getPdfInfo()) )
                .build();
    }
}
