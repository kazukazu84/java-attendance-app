package com.example.salary.salarydetail.validation;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = SalaryDetailRequiredValidator.class)
@Target({ FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface SalaryDetailRequired {

    String message() default "必須項目です";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
