package com.msa.banking.product.presentation.controller;

import com.msa.banking.common.response.SuccessResponse;
import com.msa.banking.commonbean.annotation.LogDataChange;
import com.msa.banking.product.application.dto.ResponsePDFInfo;
import com.msa.banking.product.application.service.PDFInfoApplicationService;
import com.msa.banking.product.application.service.UploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class PdfInfoController {

    private final PDFInfoApplicationService pdfInfoService;

    @Operation(summary = "pdf 저장 api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "pdf 파일 저장 성공"),
            @ApiResponse(responseCode = "401", description = "권한이 없음"),
            @ApiResponse(responseCode = "500", description = "등록 중 실패")
    })
    @PostMapping(value = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @LogDataChange
    // TODO: 관리자만 접근 가능하도록 @hasAnyAuthority() 설정 해야함
    public ResponseEntity<?> UploadPdf(@RequestPart("pdf") MultipartFile pdfFile) {
        // 어플리케이션 계층 서비스 호츌
        Long id = pdfInfoService.createPDFInfo(pdfFile);

        SuccessResponse response = SuccessResponse.builder()
                .resultCode(HttpStatus.OK.value())
                .resultMessage("pdf를 저장했습니다.")
                .data(id)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "pdf 다운로드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "pdf 파일"),
            @ApiResponse(responseCode = "401", description = "권한이 없음"),
            @ApiResponse(responseCode = "404", description = "파일 없음"),
            @ApiResponse(responseCode = "500", description = "다운 실패")
    })
    @GetMapping("/download/pdf")
    // TODO: 인증자만 접근 가능하도록 @hasAnyAuthority() 설정 해야함
    public ResponseEntity<InputStreamResource> getImage(@RequestParam(value = "file") Long fileName) throws IOException {

        ResponsePDFInfo pdfInfo = pdfInfoService.getPdf(fileName);

        String encodedFileName = URLEncoder.encode(pdfInfo.getFilaName(), StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(pdfInfo.getFileData()));
    }
}
