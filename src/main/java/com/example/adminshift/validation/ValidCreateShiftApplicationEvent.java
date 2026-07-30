package com.example.adminshift.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * 新規作成用シフト受付イベント設定の相関バリデーション用アノテーション
 */
@Documented
@Constraint(validatedBy = CreateShiftApplicationEventValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCreateShiftApplicationEvent {

    String message() default "入力内容に不備があります。";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}