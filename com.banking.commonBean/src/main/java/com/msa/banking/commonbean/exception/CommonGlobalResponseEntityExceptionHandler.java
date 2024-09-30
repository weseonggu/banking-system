package com.msa.banking.commonbean.exception;

import com.msa.banking.commonbean.exception.responsedto.FailMessage;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class CommonGlobalResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        BindingResult bindingResult = ex.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();

        List<String> errorMessages = new ArrayList<>();
        for (FieldError fieldError : fieldErrors) {
            errorMessages.add(fieldError.getDefaultMessage());
        }

        FailMessage message = new FailMessage(LocalDateTime.now(), request.getDescription(false), errorMessages);
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    /**
     * 직접 설정한 예외를 제외한 모든 예외를 처리하는 메소드
     * @param ex
     * @param request
     * @return
     * @throws Exception
     */
    @ExceptionHandler(Exception.class)
    public final ResponseEntity<FailMessage> handeleCommonException(Exception ex, WebRequest request) throws Exception{
        FailMessage message = new FailMessage(LocalDateTime.now(), request.getDescription(false), List.of("잠시후 다시 시도해주세요"));
        return new ResponseEntity<FailMessage>(message,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 직접 설정한 예외를 제외한 모든 예외를 처리하는 메소드
     * @param ex
     * @param request
     * @return
     * @throws Exception
     */
    @ExceptionHandler(RuntimeException.class)
    public final ResponseEntity<FailMessage> handeleCommonRuntimeException(Exception ex, WebRequest request) throws Exception{
        FailMessage message = new FailMessage(LocalDateTime.now(), request.getDescription(false), List.of("잠시후 다시 시도해주세요"));
        return new ResponseEntity<FailMessage>(message,HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
