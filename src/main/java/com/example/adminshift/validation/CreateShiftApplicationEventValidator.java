package com.example.adminshift.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.springframework.stereotype.Component;

import com.example.adminshift.form.CreateShiftApplicationEventForm;

/**
 * 新規作成用シフト受付イベント設定の相関チェックを行うValidatorクラス
 */
@Component
public class CreateShiftApplicationEventValidator implements ConstraintValidator<ValidCreateShiftApplicationEvent, CreateShiftApplicationEventForm> {

    @Override
    public boolean isValid(CreateShiftApplicationEventForm form, ConstraintValidatorContext context) {
        if (form == null) {
            return true;
        }

        Integer startDays = form.getApplicationStartDays();
        Integer endDays = form.getApplicationEndDays();

        // 未入力（null）の場合は@NotNull等他のアノテーションに任せるため判定スキップ
        if (startDays == null || endDays == null) {
            return true;
        }

        boolean isValid = true;

        // minusDaysで過去日を算出するため、「開始日(○日前) >= 締切日(○日前)」である必要があります。
        // （例: 14日前 - 7日前 => 開始: T-14日, 締切: T-7日 で正しい順序）
        if (startDays < endDays) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("受付開始日は受付終了日以前で入力してください。")
                   .addPropertyNode("applicationStartDays")
                   .addConstraintViolation();
            isValid = false;
        }

        return isValid;
    }
}