package com.example.yin.annotation.validated;

import com.example.yin.annotation.ByteEnumRule;
import com.example.yin.annotation.ByteSizeRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.UnsupportedEncodingException;


@Slf4j
public class ByteSizeRuleValidate  implements ConstraintValidator<ByteSizeRule, String> {

    int size;
    boolean notNull;

    @Override
    public void initialize(ByteSizeRule constraintAnnotation) {
        this.size = constraintAnnotation.size();
        this.notNull = constraintAnnotation.notNull();
    }

    @Override
    public boolean isValid(String input, ConstraintValidatorContext constraintValidatorContext) {
        //如果允许为null，且又为空，则直接返回就行了
        if (!notNull && ObjectUtils.isEmpty(input))
            return true;

        if (ObjectUtils.isEmpty(input)) return false;

        try {
            byte[] bytes = input.getBytes("utf-8");
            int length = bytes.length;
            if (length > size) return false;
            return true;
        } catch (UnsupportedEncodingException e) {
            return false;
        }
    }
}
