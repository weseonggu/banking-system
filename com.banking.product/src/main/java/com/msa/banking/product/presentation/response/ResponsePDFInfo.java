package com.msa.banking.product.presentation.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.InputStream;
import java.io.Serializable;

@Getter
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class ResponsePDFInfo implements Serializable {
    long fileId;
    String filaName;
    InputStream fileData;

}
