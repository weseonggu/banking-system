package com.msa.banking.product.application.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface UploadService {
    String uploadImage(MultipartFile multipartFile);
    InputStream getFile(String fileName);
}
