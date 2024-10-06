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
    // 400 Bad Request
    PASSWORD_BAD_REQUEST(HttpStatus.BAD_REQUEST, 2002, "Incorrect password."),
    // 403 Forbidden
    USER_FORBIDDEN(HttpStatus.FORBIDDEN, 2003, "본인 정보만 접근 가능합니다."),
    // 400 Bad Request
    ADDRESS_BAD_REQUEST(HttpStatus.BAD_REQUEST, 2004, "city, street, zipcode 모든 필드가 작성되어야 합니다.");

    /* 계좌 3000번대 */
    /* 알림 4000번대 */
    /* 개인내역 5000번대 */
    // 404 Not Found 자원을 찾을 수 없습니다.
//    PERSONAL_HISTORY_NOT_FOUND(HttpStatus.NOT_FOUND, 5001, "개인 내역을 찾을 수 없습니다.");
    /* 상품 6000번대 */
    /* 문의사항 7000번대 */

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
