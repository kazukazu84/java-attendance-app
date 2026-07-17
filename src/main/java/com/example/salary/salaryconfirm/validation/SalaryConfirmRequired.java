package com.example.salary.salaryconfirm.validation;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = SalaryConfirmRequiredValidator.class)
@Target({ FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface SalaryConfirmRequired {

    String message() default "必須項目です";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
