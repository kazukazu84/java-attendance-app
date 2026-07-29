package com.example.adminshift.validation;

import java.time.LocalDate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.example.adminshift.form.ShiftApplicationDateHolder;

public class ShiftApplicationEventDateValidator implements ConstraintValidator<ValidShiftApplicationEventDate, ShiftApplicationDateHolder> {

    @Override
    public boolean isValid(ShiftApplicationDateHolder form, ConstraintValidatorContext context) {
        if (form == null) {
            return true;
        }

        LocalDate startDate = form.getApplicationStartDate();
        LocalDate endDate = form.getApplicationEndDate();

        // 単体チェック（@NotNullなど）でエラーになる場合はここではチェックをスキップ
        if (startDate == null || endDate == null) {
            return true;
        }

        // 受付開始日が受付終了日より後（未来）になっている場合はエラー
        if (startDate.isAfter(endDate)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                   .addPropertyNode("applicationStartDays") // 画面のフィールドに紐付ける場合（必要に応じて）
                   .addConstraintViolation();
            return false;
        }

        return true;
    }
}