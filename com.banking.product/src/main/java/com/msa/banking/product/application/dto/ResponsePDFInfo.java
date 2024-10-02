package com.msa.banking.product.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.InputStream;
@Getter
@AllArgsConstructor
public class ResponsePDFInfo {
    long fileId;
    String filaName;
    InputStream fileData;
}
