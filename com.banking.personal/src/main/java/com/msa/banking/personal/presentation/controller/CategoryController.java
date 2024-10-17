package com.msa.banking.personal.presentation.controller;

import com.msa.banking.common.response.SuccessCode;
import com.msa.banking.common.response.SuccessResponse;
import com.msa.banking.commonbean.security.UserDetailsImpl;
import com.msa.banking.personal.application.dto.category.CategoryListDto;
import com.msa.banking.personal.application.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "카테고리 서비스")
@Log4j2(topic = "CategoryController")
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 본인이 생성한 카테고리 목록 조회
     */
    @GetMapping("")
    @Operation(summary = "생성한 카테고리 목록 조회", description = "본인이 생성한 카테고리 목록 조회 API 입니다.")
    public ResponseEntity<?> getCategoryList(@AuthenticationPrincipal UserDetailsImpl userDetails){

        UUID userId = userDetails.getUserId();

        List<CategoryListDto> categoryList = categoryService.getCategoryList(userId);

        return ResponseEntity.ok(
                new SuccessResponse<>(SuccessCode.SELECT_SUCCESS.getStatus(), "getCategoryList", categoryList));
    }
}
