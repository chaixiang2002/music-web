package com.example.yin.annotation.validated;

import com.example.yin.annotation.ByteEnumRule;
import com.example.yin.annotation.RegularRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
public class ByteEnumRuleValidate implements ConstraintValidator<ByteEnumRule, Byte> {

    String rule;
    boolean notNull;

    @Override
    public void initialize(ByteEnumRule constraintAnnotation) {
        this.rule = constraintAnnotation.rule();
        this.notNull = constraintAnnotation.notNull();
    }

    @Override
    public boolean isValid(Byte bytes, ConstraintValidatorContext constraintValidatorContext) {
        //如果允许为null，且又为空，则直接返回就行了
        if (!notNull && ObjectUtils.isEmpty(bytes))
            return true;

        if(ObjectUtils.isEmpty(bytes)) return false;
        String[] split = rule.split(",");
        //有其中一个则返回正确
        for (int i = 0; i < split.length; i++) {
            if (bytes.byteValue() == Byte.valueOf(split[i])) {
                return true;
            }
        }
        return false;
    }
}
