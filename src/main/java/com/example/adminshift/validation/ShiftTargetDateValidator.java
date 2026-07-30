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

        if (form.getTargetStartDate() != null && form.getTargetEndDate() != null) {
            // 対象期間開始日 > 対象期間終了日 の場合エラー
            if (form.getTargetStartDate().isAfter(form.getTargetEndDate())) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("対象期間開始日は対象期間終了日より前の日付を指定してください。")
                       .addPropertyNode("targetStartDate") // HTMLのtargetStartDate欄にエラーを紐付け
                       .addConstraintViolation();
                return false;
            }
        }
        return true;
    }
}