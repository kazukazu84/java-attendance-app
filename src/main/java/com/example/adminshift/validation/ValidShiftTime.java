package com.example.adminshift.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * シフト時間の相関バリデーション用アノテーション
 */
@Documented
@Constraint(validatedBy = ShiftTimeValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidShiftTime {

    String message() default "出勤の場合は出勤時間と退勤時間の両方を入力してください。";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}