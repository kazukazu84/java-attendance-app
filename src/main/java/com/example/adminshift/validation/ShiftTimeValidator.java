package com.example.adminshift.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.example.adminshift.form.ShiftForm;

/**
 * 出勤時の時間入力チェックを行うValidatorクラス
 */
public class ShiftTimeValidator implements ConstraintValidator<ValidShiftTime, ShiftForm> {

    @Override
    public boolean isValid(ShiftForm form, ConstraintValidatorContext context) {
        if (form == null) {
            return true;
        }

        // 休み（rest == true または isAvailable == 0）の場合はチェックを行わない（任意入力）
        if (form.isRest() || Integer.valueOf(0).equals(form.getIsAvailable())) {
            return true;
        }

        // 出勤（rest == false）の場合の必須チェック
        boolean isValid = true;

        // デフォルトのエラーメッセージ生成を無効化（フィールド単位でメッセージを割り当てるため）
        context.disableDefaultConstraintViolation();

        if (form.getStartTime() == null) {
            context.buildConstraintViolationWithTemplate("出勤予定時間を入力してください。")
                   .addPropertyNode("startTime")
                   .addConstraintViolation();
            isValid = false;
        }

        if (form.getEndTime() == null) {
            context.buildConstraintViolationWithTemplate("退勤予定時間を入力してください。")
                   .addPropertyNode("endTime")
                   .addConstraintViolation();
            isValid = false;
        }

        return isValid;
    }
}
