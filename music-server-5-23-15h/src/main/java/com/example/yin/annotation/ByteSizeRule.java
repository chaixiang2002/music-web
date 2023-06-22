package com.example.yin.annotation;

import com.example.yin.annotation.validated.ByteSizeRuleValidate;
import com.example.yin.annotation.validated.RegularRuleValidate;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {ByteSizeRuleValidate.class})
public @interface ByteSizeRule {

    int size() default 65535;

    boolean notNull() default false;

    String message() default "{javax.validation.constraints.NotEmpty.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
