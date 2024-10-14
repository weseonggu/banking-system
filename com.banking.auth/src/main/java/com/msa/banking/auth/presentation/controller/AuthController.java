package com.msa.banking.auth.presentation.controller;

import com.msa.banking.auth.application.service.AuthService;
import com.msa.banking.auth.infrastructure.scheduler.Scheduler;
import com.msa.banking.auth.presentation.request.AuthResetPasswordRequestDto;
import com.msa.banking.auth.presentation.request.AuthSignInRequestDto;
import com.msa.banking.auth.presentation.request.AuthSignUpRequestDto;
import com.msa.banking.auth.presentation.request.SlackNumberRequestDto;
import com.msa.banking.auth.presentation.response.AuthResponseDto;
import com.msa.banking.common.auth.dto.SlackIdRequestDto;
import com.msa.banking.common.response.SuccessCode;
import com.msa.banking.common.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "인증 서비스", description = "회원가입, 로그인, 로그아웃 등 인증 서비스 관련 API 입니다.")
public class AuthController {

    private final AuthService authService;
    private final Scheduler scheduler;

    /**
     * 회원가입
     *
     * @param request
     * @return
     */
    @PostMapping("/signUp")
    @Operation(summary = "회원가입", description = "회원가입을 진행하는 API 입니다.")
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
    @Operation(summary = "로그인", description = "로그인을 진행하는 API 입니다.")
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
    @Operation(summary = "로그아웃", description = "로그아웃을 진행하는 API 입니다.")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        log.info("로그아웃 시도 중 ");

        authService.logout(request);

        log.info("JWT 토큰이 블랙리스트에 추가되었습니다");
        return ResponseEntity.ok(new SuccessResponse<>(SuccessCode.UPDATE_SUCCESS.getStatus(), "logout success", "로그아웃 완료"));
    }

    /**
     * 고객 비밀번호 초기화
     *
     * @param request
     * @return
     */
    @PostMapping("/reset-password")
    @Operation(summary = "고객 비밀번호 초기화", description = "가입한 고객의 비밀번호를 초기화하는 API 입니다.")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody AuthResetPasswordRequestDto request) {
        log.info("비밀번호 초기화 시도 중 | request: {}", request);

        authService.resetPassword(request);

        log.info("비밀번호 초기화 완료");
        return ResponseEntity.ok(new SuccessResponse<>(SuccessCode.UPDATE_SUCCESS.getStatus(), "password reset success", "비밀번호 초기화 완료"));
    }

    /**
     * 슬랙 인증 번호 발송
     *
     * @param request
     * @return
     */
    @PostMapping("/slack-code")
    public ResponseEntity<?> slackCheck(@Valid @RequestBody SlackIdRequestDto request) {
        log.info("슬랙 ID 인증번호 발급 시도 중 | slackId: {}", request.getSlackId());

        String response = authService.slackCheck(request);

        log.info("슬랙 ID 인증번호 발급 완료 | response: {}", response);
        return ResponseEntity.ok(new SuccessResponse<>(SuccessCode.INSERT_SUCCESS.getStatus(), "Successfully Issued", response));
    }

    /**
     * 슬랙 인증 번호 검증
     * @param request
     * @return
     */
    @PostMapping("/slack-valid")
    public ResponseEntity<?> slackCheckValid(@Valid @RequestBody SlackNumberRequestDto request) {
        log.info("슬랙 인증번호 검증 시도 중 | slackNumber: {}", request.getSlackNumber());

        String response = authService.slackCheckValid(request);

        return ResponseEntity.ok(new SuccessResponse<>(SuccessCode.SELECT_SUCCESS.getStatus(), "Successfully Valid", response));
    }

    @GetMapping
    public void test() {
        scheduler.findAllMaster();
    }
}
