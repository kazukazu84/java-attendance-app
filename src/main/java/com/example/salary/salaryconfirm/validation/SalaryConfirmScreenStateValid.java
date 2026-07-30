package com.example.salary.salaryconfirm.validation;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = SalaryConfirmScreenStateValidator.class)
@Target({ TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface SalaryConfirmScreenStateValid {

    String message() default "画面状態が不正です";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}