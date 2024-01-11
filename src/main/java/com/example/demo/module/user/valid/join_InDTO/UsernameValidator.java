package com.example.demo.module.user.valid.join_InDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UsernameValidator implements ConstraintValidator<ValidUsername, String> {

    @Override
    public void initialize(ValidUsername constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("아이디를 입력해주세요")
                    .addConstraintViolation();
            return false;
        }

        if (6 < value.length()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("6글자 이내로 입력해주세요")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
