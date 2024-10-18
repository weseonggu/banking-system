package com.msa.banking.commonbean.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = NumberRatioValidator.class)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidNumberRatio {
    String message() default "The ratio of digits in the string must not exceed the specified limit.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    double maxRatio() default 0.5; // 최대 숫자 비율, 기본값 50%
}