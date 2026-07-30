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
 *
 * 以下をチェックします。
 * ・出勤の場合は開始・終了時刻が入力されていること
 * ・開始時刻と終了時刻が同じでないこと
 */
@Documented
@Constraint(validatedBy = ShiftTimeValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidShiftTime {

    /**
     * デフォルトメッセージ
     * （Validator側で個別メッセージを設定するため通常は表示されません）
     */
    String message() default "シフト時間が正しくありません。";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}