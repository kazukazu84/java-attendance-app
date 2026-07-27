package com.example.adminshift.validation;

import java.time.LocalDate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.springframework.stereotype.Component;

import com.example.adminshift.form.UpdateShiftApplicationEventForm;

/**
 * シフト受付イベントの日付妥当性チェックを行うValidatorクラス
 */
@Component
public class ShiftApplicationEventDateValidator implements ConstraintValidator<ValidShiftApplicationEventDate, UpdateShiftApplicationEventForm> {

    @Override
    public boolean isValid(UpdateShiftApplicationEventForm form, ConstraintValidatorContext context) {
        if (form == null) {
            return true;
        }

        LocalDate targetStartDate = form.getTargetStartDate();
        LocalDate targetEndDate = form.getTargetEndDate();
        LocalDate applicationStartDate = form.getApplicationStartDate();
        LocalDate applicationEndDate = form.getApplicationEndDate();

        // 必須チェック（未入力の場合は他のアノテーションまたはNull安全のために判定スキップ）
        if (targetStartDate == null || targetEndDate == null || applicationStartDate == null || applicationEndDate == null) {
            return true;
        }

        boolean isValid = true;

        // デフォルトのエラーメッセージ生成を無効化
        context.disableDefaultConstraintViolation();

        // 条件①：対象期間開始日 ＞ 対象期間終了日
        if (targetStartDate.isAfter(targetEndDate)) {
            context.buildConstraintViolationWithTemplate("対象期間開始日は対象期間終了日以前で入力してください。")
                   .addPropertyNode("targetStartDate")
                   .addConstraintViolation();
            isValid = false;
        }

        // 条件②：受付開始日 ＞ 受付終了日
        if (applicationStartDate.isAfter(applicationEndDate)) {
            context.buildConstraintViolationWithTemplate("受付開始日は受付終了日以前で入力してください。")
                   .addConstraintViolation();
            isValid = false;
        }

        // 条件③：受付開始日 ＞＝ 対象期間開始日
        if (!applicationStartDate.isBefore(targetStartDate)) {
            context.buildConstraintViolationWithTemplate("受付期間は対象期間開始日より前の日付で設定してください。")
                   .addPropertyNode("applicationStartDate")
                   .addConstraintViolation();
            isValid = false;
        }

        // 条件④：受付終了日 ＞＝ 対象期間開始日
        if (!applicationEndDate.isBefore(targetStartDate)) {
            context.buildConstraintViolationWithTemplate("受付期間は対象期間開始日より前の日付で設定してください。")
                   .addConstraintViolation();
            isValid = false;
        }

        return isValid;
    }
}