package com.msa.banking.auth.presentation.controller;

import com.msa.banking.auth.application.service.AuthService;
import com.msa.banking.auth.presentation.request.AuthResetPasswordRequestDto;
import com.msa.banking.auth.presentation.request.AuthSignInRequestDto;
import com.msa.banking.auth.presentation.request.AuthSignUpRequestDto;
import com.msa.banking.auth.presentation.response.AuthResponseDto;
import com.msa.banking.common.response.SuccessCode;
import com.msa.banking.common.response.SuccessResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
     *
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
     *
     * @param request
     * @return
     */
    @PostMapping("/signIn")
    public ResponseEntity<?> singInAuth(@Valid @RequestBody AuthSignInRequestDto request,
                                        HttpServletResponse response) {
        log.info("로그인 시도 중 | request: {}", request);

        String token = authService.signInAuth(request, response);

        log.info("로그인 성공 | request: {}, token: {}", request, token);
        return ResponseEntity.ok(new SuccessResponse<>(SuccessCode.SELECT_SUCCESS.getStatus(), "user logged in", token));
    }

    /**
     * 로그아웃
     *
     * @param request
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        log.info("로그아웃 시도 중 ");

        authService.logout(request);

        log.info("JWT 토큰이 블랙리스트에 추가되었습니다");
        return ResponseEntity.ok(new SuccessResponse<>(SuccessCode.UPDATE_SUCCESS.getStatus(), "logout success", "로그아웃 완료"));
    }

    /**
     * 고객 비밀번호 초기화
     * @param request
     * @return
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody AuthResetPasswordRequestDto request) {
        log.info("비밀번호 초기화 시도 중 | request: {}", request);

        authService.resetPassword(request);

        log.info("비밀번호 초기화 완료");
        return ResponseEntity.ok(new SuccessResponse<>(SuccessCode.UPDATE_SUCCESS.getStatus(), "password reset success", "비밀번호 초기화 완료"));
    }
}
