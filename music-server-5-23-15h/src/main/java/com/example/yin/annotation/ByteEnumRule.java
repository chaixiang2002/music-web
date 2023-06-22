package com.example.yin.annotation;

import com.example.yin.annotation.validated.ByteEnumRuleValidate;
import com.example.yin.annotation.validated.RegularRuleValidate;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {ByteEnumRuleValidate.class})
public @interface ByteEnumRule {

    String rule();

    boolean notNull() default false;

    String message() default "{javax.validation.constraints.NotEmpty.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
