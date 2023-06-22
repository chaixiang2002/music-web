package com.example.yin.annotation.validated;

import com.example.yin.annotation.RegularRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class RegularRuleValidate implements ConstraintValidator<RegularRule, String> {

    String rule;
    boolean notNull;
    @Override
    public void initialize(RegularRule constraintAnnotation) {
        this.rule = constraintAnnotation.rule();
        this.notNull = constraintAnnotation.notNull();
    }

    @Override
    public boolean isValid(String input, ConstraintValidatorContext constraintValidatorContext) {
        //如果允许为null，且又为空，则直接返回就行了
        if (!notNull && ObjectUtils.isEmpty(input))
            return true;
        log.info("数据校验入参args=" + input);

        if(ObjectUtils.isEmpty(input)) return false;
        Pattern p = Pattern.compile(rule);
        Matcher m = p.matcher(input);
        return m.matches();
    }


}
