package com.msa.banking.auth.presentation.controller;

import com.msa.banking.auth.application.service.AuthService;
import com.msa.banking.auth.presentation.request.AuthSignInRequestDto;
import com.msa.banking.auth.presentation.request.AuthSignUpRequestDto;
import com.msa.banking.auth.presentation.response.AuthResponseDto;
import com.msa.banking.common.response.SuccessCode;
import com.msa.banking.common.response.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Slf4j(topic = "Auth")
public class AuthController {

    private final AuthService authService;

    /**
     * 회원가입
     * @param request
     * @return
     */
    @PostMapping("/signUp")
    public ResponseEntity<?> createAuth(@Valid @RequestBody AuthSignUpRequestDto request) {
        log.info("회원가입 시도 중 | request: {}", request);

        AuthResponseDto response = authService.createAuth(request);

        log.info("회원가입 성공 | response: {}", response);
        return ResponseEntity.ok(new SuccessResponse<>(SuccessCode.INSERT_SUCCESS.getStatus(), "user created", response));
    }

    /**
     * 로그인
     * @param request
     * @return
     */
    @PostMapping("/signIn")
    public ResponseEntity<?> singInAuth(@Valid @RequestBody AuthSignInRequestDto request) {
        log.info("로그인 시도 중 | request: {}", request);

        String token = authService.signInAuth(request);

        log.info("로그인 성공 | request: {}, token: {}", request, token);
        return ResponseEntity.ok(new SuccessResponse<>(SuccessCode.SELECT_SUCCESS.getStatus(), "user logged in", token));
    }


}
