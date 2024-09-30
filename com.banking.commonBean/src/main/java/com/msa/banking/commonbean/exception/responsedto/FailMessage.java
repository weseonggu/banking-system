package com.msa.banking.commonbean.exception.responsedto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class FailMessage {
    private LocalDateTime timeStamp;
    private String endPoint;
    private List<String> errorDetails;
}
