package com.msa.banking.commonbean.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j(topic = "DataChage")
public class LogChageDataAspect {


    @Around("@annotation(com.msa.banking.commonbean.annotation.LogDataChange)")
    public Object createMemberAround(ProceedingJoinPoint joinPoint) throws Throwable {
        return redundantMethods(joinPoint);
    }

    private Object redundantMethods(ProceedingJoinPoint joinPoint)throws Throwable{
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        Object[] args = joinPoint.getArgs();
        // 회원 가입저장 전 저장할 데이터
        log.info("Method {} in class {} is about to be executed with arguments: {}",
                methodName, className, args);
        Object result;
        try {
            // 메소드 실행
            result = joinPoint.proceed();
            // 데이터 변경 성공 로그
            log.info("Method {} in class {} executed successfully with return value: {}",
                    methodName, className, result);
        } catch (Exception ex) {
            // 데이터 변경 로그
            log.warn("Method {} in class {} threw an exception: {}",
                    methodName, className, ex.getMessage());
            // 예외를 다시 던져 전역 예외 처리기로 넘김
            throw ex;
        }
        return result;
    }
}
