package com.msa.banking.product.presentation.response;

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
