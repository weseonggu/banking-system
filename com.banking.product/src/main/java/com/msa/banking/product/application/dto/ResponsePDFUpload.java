package com.msa.banking.product.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResponsePDFUpload {
    Long id;
    private String uploadFileName;
}
