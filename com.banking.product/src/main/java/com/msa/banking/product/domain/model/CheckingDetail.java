package com.msa.banking.product.domain.model;

import com.msa.banking.common.base.AuditEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "p_checking_detail")
public class CheckingDetail extends AuditEntity {

    @Id
    @UuidGenerator
    @Column(name = "checking_detail_id")
    private UUID id;

    @Column(columnDefinition = "TEXT", name = "checking_detail", nullable = false)
    private String checkingDetail;

    @Column(name = "terms_and_conditions", nullable = false)
    private String termsAndConditions;

    @Column(precision = 5, scale = 4, name = "interest_rate", nullable = false)
    private BigDecimal interestRate;

    @Column(name = "fees", nullable = false)
    private int fees;

    ///////////////////////////////////////////////////////////////////////

    @OneToOne(mappedBy = "checkingDetail", fetch = FetchType.LAZY)
    private Product product;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "pdf_info_id", referencedColumnName = "pdf_info_id")
    private PDFInfo pdfInfo;

    //////////////////////////////////////////////////////////////////////

    public static CheckingDetail create(String checkingDetail, String termsAndConditions,
                                        BigDecimal interestRate, int fees) {
        return CheckingDetail.builder()
                .checkingDetail(checkingDetail)
                .termsAndConditions(termsAndConditions)
                .interestRate(interestRate)
                .fees(fees)
                .build();

    }

    public CheckingDetail addPDF(PDFInfo pdfInfo) {
        this.pdfInfo = pdfInfo;
        return this;
    }

}
