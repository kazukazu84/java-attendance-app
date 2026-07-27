package com.example.adminshift.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * シフト受付イベント日付の相関バリデーション用アノテーション
 */
@Documented
@Constraint(validatedBy = ShiftApplicationEventDateValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidShiftApplicationEventDate {

    String message() default "日付の入力内容に不備があります。";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}