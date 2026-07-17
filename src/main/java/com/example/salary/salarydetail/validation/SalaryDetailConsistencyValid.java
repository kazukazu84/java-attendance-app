package com.example.salary.salarydetail.validation;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = SalaryDetailConsistencyValidator.class)
@Target({ TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface SalaryDetailConsistencyValid {

    String message() default "整合性が不正です";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}

