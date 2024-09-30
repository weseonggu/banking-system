package com.msa.banking.common.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
public class ErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    public ErrorResponse(int status, String error, String message, String path) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        this.timestamp = LocalDateTime.parse(LocalDateTime.now().format(formatter));
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }
}
