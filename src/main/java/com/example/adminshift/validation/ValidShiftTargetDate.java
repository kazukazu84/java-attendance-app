package com.example.adminshift.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ShiftTargetDateValidator.class)
@Documented
public @interface ValidShiftTargetDate {
    String message() default "対象期間開始日は対象期間終了日より前の日付を指定してください。";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}