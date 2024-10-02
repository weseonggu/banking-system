package com.msa.banking.product.infrastructure.s3;

import com.msa.banking.product.application.service.UploadService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;

@Service
@RefreshScope
public class S3UploadService implements UploadService{
    private final S3Client s3Client;
    private final String bucketName;
    private final String s3Path;

    public S3UploadService(
            S3Client s3Client,
            @Value("${s3.bucket-name}") String bucketName,
            @Value("${s3.path}") String s3Path
    ) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.s3Path = s3Path;
    }

    // s3 파일 업로드
    @Override
    @SneakyThrows
    public String uploadImage(MultipartFile multipartFile) {
        String uploadUrl = getS3UploadUrl(multipartFile.getOriginalFilename());
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(uploadUrl)
                .build();

        RequestBody content = RequestBody.fromInputStream(
                multipartFile.getInputStream(),
                multipartFile.getSize()
        );

        s3Client.putObject(request, content);
        return uploadUrl;
    }

    // s3에서 파일 다운로드
    public InputStream getFile(String fileName) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(getS3UploadUrl(fileName))
                .build();
        return s3Client.getObject(request);
    }

    private String getS3UploadUrl(String fileName) {
        return s3Path + "/" + fileName;
    }
}
