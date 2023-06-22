package com.example.yin.annotation.validated;


import com.example.yin.annotation.StringLengthRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Slf4j
public class StringLengthValidate implements ConstraintValidator<StringLengthRule, String> {
    private int min = 0;
    private int max = 10;
    boolean notNull;
    @Override
    public boolean isValid(String input, ConstraintValidatorContext constraintValidatorContext) {
        //如果允许为null，且又为空，则直接返回就行了
        if (!notNull && ObjectUtils.isEmpty(input))
            return true;
        if(ObjectUtils.isEmpty(input)) return false;
        int length = input.length();
        if (length >= min && length <= max) return true;
        else return false;
    }

    @Override
    public void initialize(StringLengthRule constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
        this.notNull = constraintAnnotation.notNull();
    }
}
