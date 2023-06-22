package com.example.yin.annotation;

import com.example.yin.annotation.validated.RegularRuleValidate;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {RegularRuleValidate.class})
public @interface RegularRule {

    String rule() default "";
    boolean notNull()default false;
    String message() default "{javax.validation.constraints.NotEmpty.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
