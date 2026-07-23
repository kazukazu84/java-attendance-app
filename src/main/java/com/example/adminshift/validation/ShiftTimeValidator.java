package com.example.adminshift.validation;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.springframework.stereotype.Component;

import com.example.adminshift.entity.Shift;
import com.example.adminshift.form.ShiftForm;
import com.example.adminshift.repository.ShiftRepository;

import lombok.RequiredArgsConstructor;

/**
 * 出勤時の時間入力チェックおよび夜勤時の翌日重複チェックを行うValidatorクラス
 */
@Component
@RequiredArgsConstructor
public class ShiftTimeValidator implements ConstraintValidator<ValidShiftTime, ShiftForm> {

    private final ShiftRepository shiftRepository;

    @Override
    public boolean isValid(ShiftForm form, ConstraintValidatorContext context) {
        if (form == null) {
            return true;
        }

        // 休み（rest == true または isAvailable == 0）の場合はチェックを行わない
        if (form.isRest() || Integer.valueOf(0).equals(form.getIsAvailable())) {
            return true;
        }

        boolean isValid = true;

        // デフォルトのエラーメッセージ生成を無効化
        context.disableDefaultConstraintViolation();

        LocalTime startTime = form.getStartTime();
        LocalTime endTime = form.getEndTime();

        // 必須チェック
        if (startTime == null) {
            context.buildConstraintViolationWithTemplate("出勤予定時間を入力してください。")
                   .addPropertyNode("startTime")
                   .addConstraintViolation();
            isValid = false;
        }

        if (endTime == null) {
            context.buildConstraintViolationWithTemplate("退勤予定時間を入力してください。")
                   .addPropertyNode("endTime")
                   .addConstraintViolation();
            isValid = false;
        }

        if (!isValid) {
            return false;
        }

        // 夜勤チェック（startTime > endTime）
        if (startTime.isAfter(endTime)) {
            LocalDate nextDate = form.getShiftDate().plusDays(1);

            // 翌日のシフトレコードを取得
            Shift nextShift = shiftRepository.findByEventIdAndUserIdAndShiftDate(
                    form.getEventId(),
                    form.getUserId(),
                    nextDate
            ).orElse(null);

            // 翌日シフトが存在し、かつ「出勤 (isAvailable == 1)」の場合のみ比較対象
            if (nextShift != null && Integer.valueOf(1).equals(nextShift.getIsAvailable())) {
                LocalTime nextStart = nextShift.getStartTime();

                // 翌日の開始時間 <= 夜勤終了時間 の場合はエラー（終了時間と同じ刻刻も不可）
                if (nextStart != null && !nextStart.isAfter(endTime)) {
                    context.buildConstraintViolationWithTemplate(
                            "夜勤の終了時間（" + endTime + "）が翌日のシフト開始時間（" + nextStart + "）と重複・連続しているため登録できません。"
                    ).addPropertyNode("endTime").addConstraintViolation();
                    isValid = false;
                }
            }
        }

        return isValid;
    }
}