package com.msa.banking.auth.presentation.controller;

import com.msa.banking.auth.application.service.UserService;
import com.msa.banking.auth.presentation.request.AuthRequestDto;
import com.msa.banking.auth.presentation.request.SearchRequestDto;
import com.msa.banking.auth.presentation.response.AuthResponseDto;
import com.msa.banking.common.response.SuccessCode;
import com.msa.banking.common.response.SuccessResponse;
import com.msa.banking.commonbean.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Slf4j
@Tag(name = "회원 서비스", description = "회원에 관련된 API 입니다.")
public class UserController {
    // TODO 회원 삭제 보류
    private final UserService userService;

    /**
     * 내부 API 직원 테이블 전용
     * username 유저 단 건 조회
     *
     * @param userId
     * @return
     */
    @GetMapping("/employee/info")
    @Hidden
    public AuthResponseDto findEmployeeUsername(@RequestParam("userId") String userId) {
        log.info("내부 API 직원 조회 시도 중 | userId: {}", userId);

        AuthResponseDto response = userService.findEmployeeUsername(UUID.fromString(userId));

        log.info("내부 API 직원 조회 완료 | userId: {}", userId);
        return response;
    }

    /**
     * 내부 API 고객 테이블 전용
     * username 유저 단 건 조회
     *
     * @param userId
     * @return
     */
    @GetMapping("/customer/info")
    @Hidden
    public AuthResponseDto findCustomerUsername(@RequestParam("userId") String userId) {
        log.info("내부 API 고객 조회 시도 중 | userId: {}", userId);

        AuthResponseDto response = userService.findCustomerUsername(UUID.fromString(userId));

        log.info("내부 API 고객 조회 완료 | userId: {}", userId);
        return response;
    }

    /**
     * 고객 단 건 조회, 전체 허용
     *
     * @param customerId
     * @return
     */
    @GetMapping("/customer/{customer_id}")
    @Operation(summary = "고객 단 건 조회", description = "고객 단 건 조회 API 입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "customer selected"),
            @ApiResponse(responseCode = "2003", description = "본인 정보만 접근 가능합니다.")
    })
    public ResponseEntity<?> findCustomerById(@PathVariable("customer_id") UUID customerId,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info("고객 조회 시도 중 | customer_id: {}", customerId);
        UUID userId = userDetails.getUserId();
        String role = userDetails.getRole();

        AuthResponseDto response = userService.findCustomerById(customerId, userId, role);

        log.info("고객 조회 완료 | customer_id: {}", customerId);
        return ResponseEntity.ok(new SuccessResponse<>(SuccessCode.SELECT_SUCCESS.getStatus(), "customer selected", response));
    }

    /**
     * 직원 단 건 조회, 전체 허용
     * @param employeeId
     * @return
     */
    @GetMapping("/employee/{employee_id}")
    @PreAuthorize("hasAnyAuthority('MASTER', 'MANAGER')")
    @Operation(summary = "직원 단 건 조회", description = "직원 단 건 조회 API 입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "employee selected"),
            @ApiResponse(responseCode = "403", description = "접근이 거부되었습니다."),
            @ApiResponse(responseCode = "2003", description = "본인 정보만 접근 가능합니다.")
    })
    public ResponseEntity<?> findEmployeeById(@PathVariable("employee_id") UUID employeeId,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info("직원 조회 시도 중 | employee_id: {}", employeeId);
        UUID userId = userDetails.getUserId();
        String role = userDetails.getRole();

        AuthResponseDto response = userService.findEmployeeById(employeeId, userId, role);

        log.info("직원 조회 완료 | employee_id: {}", employeeId);
        return ResponseEntity.ok(new SuccessResponse<>(SuccessCode.SELECT_SUCCESS.getStatus(), "employee selected", response));
    }

    /**
     * 고객 정보 수정, 모두 허용
     * 고객 : 본인 정보만 수정 가능
     * @param customerId
     * @param request
     * @param userDetails
     * @return
     */
    @PatchMapping("/customer/{customer_id}")
    @Operation(summary = "고객 정보 수정", description = "고객 정보 수정 API 입니다. 고객은 본인의 정보만 변경할 수 있습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "customer updated"),
            @ApiResponse(responseCode = "403", description = "접근이 거부되었습니다."),
            @ApiResponse(responseCode = "400", description = "Validation Error"),
            @ApiResponse(responseCode = "2005", description = "중복된 리소스입니다."),
            @ApiResponse(responseCode = "2003", description = "본인 정보만 접근 가능합니다."),
            @ApiResponse(responseCode = "2004", description = "city, street, zipcode 모든 필드가 작성되어야 합니다.")
    })
    public ResponseEntity<?> updateCustomer(@PathVariable("customer_id") UUID customerId,
                                            @Valid @RequestBody AuthRequestDto request,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info("고객 정보 수정 시도 중 | customer_id: {}, request: {}", customerId, request);
        UUID userId = userDetails.getUserId();
        String role = userDetails.getRole();

        AuthResponseDto response = userService.updateCustomer(customerId, request, userId, role);

        log.info("고객 정보 수정 완료 | customer_id: {}, request: {}", customerId, request);
        return ResponseEntity.ok(new SuccessResponse<>(SuccessCode.UPDATE_SUCCESS.getStatus(), "customer updated", response));

    }

    /**
     * 직원 정보 수정, 마스터 매니저 허용
     * 매니저 : 본인 정보만 수정 가능
     * @param employeeId
     * @param request
     * @param userDetails
     * @return
     */
    @PatchMapping("/employee/{employee_id}")
    @PreAuthorize("hasAnyAuthority('MASTER', 'MANAGER')")
    @Operation(summary = "직원 정보 수정", description = "직원 정보 수정 API 입니다. 매니저는 본인의 정보만 변경할 수 있습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "customer updated"),
            @ApiResponse(responseCode = "403", description = "접근이 거부되었습니다."),
            @ApiResponse(responseCode = "400", description = "Validation Error"),
            @ApiResponse(responseCode = "2005", description = "중복된 리소스입니다."),
            @ApiResponse(responseCode = "2003", description = "매니저는 본인 정보만 접근 가능합니다."),
            @ApiResponse(responseCode = "2004", description = "city, street, zipcode 모든 필드가 작성되어야 합니다.")
    })
    public ResponseEntity<?> updateEmployee(@PathVariable("employee_id") UUID employeeId,
                                            @Valid @RequestBody AuthRequestDto request,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info("직원 정보 수정 시도 중 | employee_id: {}, request: {}", employeeId, request);
        UUID userId = userDetails.getUserId();
        String role = userDetails.getRole();

        AuthResponseDto response = userService.updateEmployee(employeeId, request, userId, role);

        log.info("직원 정보 수정 완료 | employee_id: {}, request: {}", employeeId, request);
        return ResponseEntity.ok(new SuccessResponse<>(SuccessCode.UPDATE_SUCCESS.getStatus(), "customer updated", response));
    }

    /**
     * 고객 전체 조회, 마스터 매니저 허용
     * @param condition
     * @param pageable
     * @return
     */
    @GetMapping("/customer")
    @PreAuthorize("hasAnyAuthority('MASTER', 'MANAGER')")
    @Operation(summary = "고객 전체 조회", description = "고객 전체 조회 API 입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "customer paging selected"),
            @ApiResponse(responseCode = "403", description = "접근이 거부되었습니다."),
    })
    public ResponseEntity<?> findAllCustomer(SearchRequestDto condition,
                                             Pageable pageable) {
        log.info("고객 정보 페이지 조회 시도 중 | condition: {}, pageable: {}", condition, pageable);

        Page<AuthResponseDto> response = userService.findAllCustomer(pageable, condition);

        log.info("고객 정보 페이지 조회 완료");
        return ResponseEntity.ok(new SuccessResponse<>(SuccessCode.SELECT_SUCCESS.getStatus(), "customer paging selected", response));
    }

    /**
     * 직원 전체 조회, 마스터 매니저 허용
     * @param condition
     * @param pageable
     * @return
     */
    @GetMapping("/employee")
    @PreAuthorize("hasAnyAuthority('MASTER', 'MANAGER')")
    @Operation(summary = "직원 전체 조회", description = "직원 전체 조회 API 입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "customer paging selected"),
            @ApiResponse(responseCode = "403", description = "접근이 거부되었습니다."),
    })
    public ResponseEntity<?> findAllEmployee(SearchRequestDto condition,
                                             Pageable pageable) {
        log.info("직원 정보 페이지 조회 시도 중 | condition: {}, pageable: {}", condition, pageable);

        Page<AuthResponseDto> response = userService.findAllEmployee(pageable, condition);

        log.info("직원 정보 페이지 조회 완료");
        return ResponseEntity.ok(new SuccessResponse<>(SuccessCode.SELECT_SUCCESS.getStatus(), "employee paging selected", response));
    }

    /**
     * 고객 ID, name 같은지 여부 확인
     * @param userId
     * @param name
     * @return
     */
    @GetMapping(value = "/customer/check")
    @PreAuthorize("isAuthenticated()")
    @Hidden
    public Boolean findByUserIdAndName(@RequestParam("userId") UUID userId, @RequestParam("name") String name){
        // 유저아이디와 name이 일치하는 데이터가 있는지 확인하는 로직을 구성해주세요.
        log.info("userId, name 일치 조회 시도 중 | userId: {}, name: {}", userId, name);

        boolean response = userService.findByUserIdAndName(userId, name);

        log.info("userId, name 일치 조회 완료 | userId: {}, name: {}", userId, name);
        return response;
    }

}
