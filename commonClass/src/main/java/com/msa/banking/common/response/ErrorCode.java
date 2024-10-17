package com.msa.banking.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    /* 글로벌 1000번대 */

    // 400 Bad Request 잘못된 요청
    BAD_REQUEST(HttpStatus.BAD_REQUEST, 1000, "잘못된 요청입니다."),
    // 401 Unauthorized 인증되지 않은 사용자
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, 1001, "인증되지 않은 사용자입니다."),
    // 403 Forbidden 접근 권한이 없습니다.
    FORBIDDEN(HttpStatus.FORBIDDEN, 1002, "접근 권한이 없습니다."),
    // 404 Not Found 자원을 찾을 수 없습니다.
    NOT_FOUND(HttpStatus.NOT_FOUND, 1003, "자원을 찾을 수 없습니다."),
    // 409 Conflict 중복된 리소스
    DUPLICATE_RESOURCE(HttpStatus.CONFLICT, 1004, "중복된 리소스입니다."),
    // 500 Internal Server Error 서버 오류
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 1005, "알 수 없는 에러가 발생하였습니다."),
    // 503 Service Unavailable
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, 1006, "서비스가 아직 준비되지 않았습니다."),

    /* 유저 2000번대 */
    // 404 Not Found
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, 2001, "user not found"),
    SLACK_NOT_VALID(HttpStatus.NOT_FOUND, 2001, "슬랙 ID 검증이 되지 않았습니다."),
    // 400 Bad Request
    EMPLOYEE_PASSWORD_BAD_REQUEST(HttpStatus.BAD_REQUEST, 2002, "Incorrect password."),
    CUSTOMER_PASSWORD_BAD_REQUEST(HttpStatus.BAD_REQUEST, 2002, "Incorrect password. 3회 이상 틀릴 경우 비밀번호 초기화를 해야합니다."),
    SLACK_VERIFICATION_CODE_ERROR(HttpStatus.BAD_REQUEST, 2002, "슬랙 인증 번호가 일치하지 않습니다."),
    SLACK_VERIFICATION_CODE_NOT_FOUND(HttpStatus.NOT_FOUND, 2002, "슬랙 ID 와 승인 번호가 일치하지 않거나, 승인 번호가 만료되었습니다. 재발급 받으세요."),
    // 403 Forbidden
    USER_FORBIDDEN(HttpStatus.FORBIDDEN, 2003, "본인 정보만 접근 가능합니다."),
    MANAGER_FORBIDDEN(HttpStatus.FORBIDDEN, 2003, "매니저는 본인 정보만 접근 가능합니다."),
    ACCOUNT_LOCKED(HttpStatus.FORBIDDEN, 2003, "로그인 실패 3회 이상으로 계정이 잠겼습니다. 비밀번호를 초기화 하세요."),
    // 400 Bad Request
    ADDRESS_BAD_REQUEST(HttpStatus.BAD_REQUEST, 2004, "city, street, zipcode 모든 필드가 작성되어야 합니다."),
    // 409 Conflict
    USERNAME_DUPLICATE_RESOURCES(HttpStatus.CONFLICT, 2005, "username 중복된 리소스입니다."),
    EMAIL_DUPLICATE_RESOURCES(HttpStatus.CONFLICT, 2005, "email 중복된 리소스입니다."),
    PHONE_NUMBER_DUPLICATE_RESOURCES(HttpStatus.CONFLICT, 2005, "phoneNumber 중복된 리소스입니다."),


    /* 계좌 3000번대 */
    // 중복 계좌
    ACCOUNT_NUMBER_ALREADY_EXISTS(HttpStatus.CONFLICT, 3001, "중복된 계좌번호입니다."),
    // 계좌 not found
    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, 3002,"해당 계좌를 찾을 수 없습니다."),
    // 자동 이체 not found
    DIRECT_DEBIT_NOT_FOUND(HttpStatus.NOT_FOUND, 3003, "해당 자동 이체 내역을 찾을 수 없습니다."),
    // 계좌 거래 내역 not found
    TRANSACTION_NOT_FOUND(HttpStatus.NOT_FOUND, 3004, "해당 계좌 거래 내역을 찾을 수 없습니다."),
    // 비밀 번호 not match
    ACCOUNT_PIN_NOT_MATCH(HttpStatus.UNAUTHORIZED, 3004, "비밀 번호가 일치하지 않습니다."),
    // 중대 시스템 오류 거래 금액 not match
    BALANCE_NOT_MATCH(HttpStatus.CONFLICT, 3007, "거래 금액이 최종 잔액과 일치하지 않습니다."),
    // 인출 불가
    WITHDRAWAL_NOT_POSSIBLE(HttpStatus.BAD_REQUEST, 3008, "인출 잔액이 모자랍니다."),
    // 이체 날짜 오류
    TRANSFER_DATE_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, 3009, "해당 이체 날짜는 설정이 불가합니다."),
    // 비밀 번호 length invalid
    INVALID_ACCOUNT_PIN_LENGTH(HttpStatus.BAD_REQUEST, 3010, "비밀 번호 6자리를 정확히 입력해주세요."),
    // 계좌 형식 invalid
    INVALID_ACCOUNT_FORMAT(HttpStatus.BAD_REQUEST, 3011, "계좌 번호 형식이 올바르지 않습니다. 계좌번호는 xxx-xxxx-xxxxxxx 형식을 따라야 합니다."),
    // 계좌 거래 트랜잭션 실패
    ACCOUNT_TRANSACTION_FAILED(HttpStatus.BAD_REQUEST , 3012, "거래 내역 처리 중에 오류가 발생했습니다."),
    // 계좌 잔액 트랜잭션 실패
    BALANCE_TRANSACTION_FAILED(HttpStatus.BAD_REQUEST, 3013, "계좌 변경 처리 중 오류가 발생했습니다."),
    // 카프카 전송 실패
    KAFKA_TRANSMIT_FAILED(HttpStatus.BAD_REQUEST, 3014,"카프카 전송 중 오류가 발생하였습니다."),
    // 카프카 전송 실패
    AMOUNT_BAD_REQUEST(HttpStatus.BAD_REQUEST, 3015,"금액은 0보다 크게 입력해야 합니다."),
    // 거래 타입 invalid
    INVALID_TRANSACTION_TYPE(HttpStatus.BAD_REQUEST, 3016,"거래 유형이 올바르지 못합니다."),



    /* 알림 4000번대 */
    SLACK_ERROR(HttpStatus.BAD_REQUEST, 4000, "슬랙 ID가 잘못되었거나, 현재 메시지를 보낼 수 없는 상태입니다."),
    /* 개인내역 5000번대 */
    // 400 Bad Request
    BUDGET_BAD_REQUEST(HttpStatus.BAD_REQUEST,5000,"잘못된 요청입니다."),
    // 404 Not Found
    PERSONAL_HISTORY_NOT_FOUND(HttpStatus.NOT_FOUND, 5001, "개인 내역을 찾을 수 없습니다."),
    // 404 Not Found
    BUDGET_NOT_FOUND(HttpStatus.NOT_FOUND, 5002, "설정한 예산 기록을 찾을 수 없습니다.");
    /* 상품 6000번대 */
    /* 문의사항 7000번대 */






    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
