package com.example.adminshift.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.example.adminshift.form.UpdateShiftApplicationEventForm;

public class ShiftTargetDateValidator 
        implements ConstraintValidator<ValidShiftTargetDate, UpdateShiftApplicationEventForm> {

    @Override
    public boolean isValid(UpdateShiftApplicationEventForm form, ConstraintValidatorContext context) {
        if (form == null) {
            return true;
        }

        boolean isValid = true;
        context.disableDefaultConstraintViolation(); // デフォルトメッセージをクリア

        // 1. 対象期間の前後チェック (targetStartDate <= targetEndDate)
        if (form.getTargetStartDate() != null && form.getTargetEndDate() != null) {
            if (form.getTargetStartDate().isAfter(form.getTargetEndDate())) {
                context.buildConstraintViolationWithTemplate("対象期間開始日は対象期間終了日より前の日付を指定してください。")
                       .addPropertyNode("targetStartDate")
                       .addConstraintViolation();
                isValid = false;
            }
        }

        // 2. イベント開始日 と 受付開始日 の整合性チェック (targetStartDate > applicationStartDate はエラー)
        if (form.getTargetStartDate() != null && form.getApplicationStartDate() != null) {
            if (!form.getTargetStartDate().isAfter(form.getApplicationStartDate())) {
                // targetStartDate <= applicationStartDate の場合（受付開始日がイベント開始日以降になっている）
                context.buildConstraintViolationWithTemplate("受付開始日は対象期間開始日より前の日付を指定してください。")
                       .addPropertyNode("applicationStartDate")
                       .addConstraintViolation();
                isValid = false;
            }
        }

        // 3. イベント開始日 と 受付終了日 の整合性チェック (targetStartDate > applicationEndDate はエラー)
        if (form.getTargetStartDate() != null && form.getApplicationEndDate() != null) {
            if (!form.getTargetStartDate().isAfter(form.getApplicationEndDate())) {
                // targetStartDate <= applicationEndDate の場合（受付終了日がイベント開始日以降になっている）
                context.buildConstraintViolationWithTemplate("受付終了日は対象期間開始日より前の日付を指定してください。")
                       .addPropertyNode("applicationEndDate")
                       .addConstraintViolation();
                isValid = false;
            }
        }

        return isValid;
    }
}