package com.msa.banking.commonbean.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NumberRatioValidator implements ConstraintValidator<ValidNumberRatio, String> {
    private double maxRatio;

    @Override
    public void initialize(ValidNumberRatio constraintAnnotation) {
        this.maxRatio = constraintAnnotation.maxRatio();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return false; // Null 또는 빈 문자열은 유효하다고 간주
        }

        long digitCount = value.chars().filter(Character::isDigit).count();
        double ratio = (double) digitCount / value.length();
        return ratio <= maxRatio; // 비율이 maxRatio 이하인지 체크
    }
}
