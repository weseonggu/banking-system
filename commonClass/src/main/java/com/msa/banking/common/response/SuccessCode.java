package com.msa.banking.common.response;

import lombok.Getter;

@Getter
public enum SuccessCode {
    // 조회 성공 코드 (HTTP Response: 200 OK)
    SELECT_SUCCESS(200),

    // 삭제 성공 코드 (HTTP Response: 200 OK)
    DELETE_SUCCESS(200),

    // 삽입 성공 코드 (HTTP Response: 201 Created)
    INSERT_SUCCESS(201),

    // 수정 성공 코드 (HTTP Response: 200 Created)
    UPDATE_SUCCESS(200);

    // 성공 코드
    private final int status;

    SuccessCode(int status) {
        this.status = status;
    }
}