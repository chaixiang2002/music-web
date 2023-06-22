package com.example.yin.annotation.validated;

import com.example.yin.annotation.ByteEnumRule;
import com.example.yin.annotation.StringEnumRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


@Slf4j
public class StringEnumRuleValidate implements ConstraintValidator<StringEnumRule, String> {

    String rules;
    boolean notNull;
    @Override
    public void initialize(StringEnumRule constraintAnnotation) {
        this.rules = constraintAnnotation.rules();
        this.notNull = constraintAnnotation.notNull();
    }

    @Override
    public boolean isValid(String input, ConstraintValidatorContext constraintValidatorContext) {
        //如果允许为null，且又为空，则直接返回就行了
        if (!notNull && ObjectUtils.isEmpty(input))
            return true;

        if(ObjectUtils.isEmpty(input)) return false;
        String[] split = rules.split(",");
        //有其中一个则返回正确
        for (int i = 0; i < split.length; i++) {
            if (input.equals(split[i])) {
                return true;
            }
        }
        return false;
    }
}
