package com.msa.banking.product.annotaion;

import com.msa.banking.product.presentation.request.RequestJoinLoan;
import com.msa.banking.product.presentation.request.RequestJoinProduct;
import com.msa.banking.product.presentation.request.RequsetJoinChecking;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PinMatchesValidator implements ConstraintValidator<PinMatches, Object> {

    public void initialize(PinMatches constraintAnnotation) {
    }
    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        if (obj instanceof RequsetJoinChecking) {
            RequsetJoinChecking request = (RequsetJoinChecking) obj;
            if (!request.getAccountPin().equals(request.getCheckPin())) {
                context.buildConstraintViolationWithTemplate("비밀번호가 일치하지 않습니다.")
                        .addNode("checkPin") // 오류 메시지를 checkPin 필드와 연관
                        .addConstraintViolation();
                return false;
            }
        } else if (obj instanceof RequestJoinLoan) {
            RequestJoinLoan request = (RequestJoinLoan) obj;
            if (!request.getAccountPin().equals(request.getCheckPin())) {
                context.buildConstraintViolationWithTemplate("비밀번호가 일치하지 않습니다.")
                        .addNode("checkPin") // 오류 메시지를 checkPin 필드와 연관
                        .addConstraintViolation();
                return false;
            }
        }
        return true;
    }
}
