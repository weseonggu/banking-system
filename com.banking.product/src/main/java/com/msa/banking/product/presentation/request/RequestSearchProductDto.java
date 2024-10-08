package com.msa.banking.product.presentation.request;

import com.msa.banking.product.lib.ProductType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RequestSearchProductDto {

    private ProductType type;

    private LocalDateTime validFrom;

    private LocalDateTime validTo;

    private Boolean is_deleted;

}
