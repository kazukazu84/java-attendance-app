package com.example.adminshift.validation;

import java.time.LocalDate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.example.adminshift.form.CreateShiftApplicationEventForm;
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

            // Formの型に応じてエラーメッセージの紐付け先フィールド名を動的に変更
            String propertyName = (form instanceof CreateShiftApplicationEventForm)
                    ? "applicationStartDays"   // 新規作成フォーム用
                    : "applicationStartDate";  // 編集フォーム用

            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                   .addPropertyNode(propertyName) // 適切なフィールドに紐付ける
                   .addConstraintViolation();

            return false;
        }

        return true;
    }
}