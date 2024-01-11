package com.example.demo.module.user.valid.join_InDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("패스워드를 입력해주세요")
                    .addConstraintViolation();
            return false;
        }

        if (value.length() < 6 || 20 < value.length() ) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("6글자 이상 20자 이내로 입력해주세요")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
