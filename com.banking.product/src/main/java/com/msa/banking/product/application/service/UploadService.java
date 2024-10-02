package com.msa.banking.product.application.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface UploadService {
    void uploadImage(String fileName, MultipartFile multipartFile);
    InputStream getFile(String fileName);
}
