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
 * 出勤時の時間入力チェックおよび夜勤時の前後日重複チェックを行うValidatorクラス
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

        // 1. 必須チェック
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

        // 2. 【パターン1】当日のシフトが「夜勤」の場合：翌日シフトの開始時間との重複チェック
        if (startTime.isAfter(endTime)) {
            LocalDate nextDate = form.getShiftDate().plusDays(1);

            Shift nextShift = shiftRepository.findByEventIdAndUserIdAndShiftDate(
                    form.getEventId(),
                    form.getUserId(),
                    nextDate
            ).orElse(null);

            // 翌日シフトが存在し、かつ「出勤 (isAvailable == 1)」の場合のみ比較
            if (nextShift != null && Integer.valueOf(1).equals(nextShift.getIsAvailable())) {
                LocalTime nextStart = nextShift.getStartTime();

                // 翌日開始時間 <= 当日夜勤終了時間 の場合はエラー（同じ時刻も不可）
                if (nextStart != null && !nextStart.isAfter(endTime)) {
                    context.buildConstraintViolationWithTemplate(
                            "夜勤の終了時間（" + endTime + "）が翌日のシフト開始時間（" + nextStart + "）と重複・連続しているため登録できません。"
                    ).addPropertyNode("endTime").addConstraintViolation();
                    isValid = false;
                }
            }
        }

        // 3. 【パターン2】前日シフトが「夜勤」の場合：前日の夜勤終了時間と当日の開始時間との重複チェック
        LocalDate prevDate = form.getShiftDate().minusDays(1);

        Shift prevShift = shiftRepository.findByEventIdAndUserIdAndShiftDate(
                form.getEventId(),
                form.getUserId(),
                prevDate
        ).orElse(null);

        // 前日シフトが存在し、かつ「出勤 (isAvailable == 1)」の場合のみ比較
        if (prevShift != null && Integer.valueOf(1).equals(prevShift.getIsAvailable())) {
            LocalTime prevStart = prevShift.getStartTime();
            LocalTime prevEnd = prevShift.getEndTime();

            // 前日シフトが「夜勤（prevStart > prevEnd）」であるか判定
            if (prevStart != null && prevEnd != null && prevStart.isAfter(prevEnd)) {
                // 前日夜勤終了時間 >= 当日開始時間 の場合はエラー（同じ時刻も不可）
                if (!startTime.isAfter(prevEnd)) {
                    context.buildConstraintViolationWithTemplate(
                            "前日の夜勤終了時間（" + prevEnd + "）と勤務時間が重複しているため登録できません。"
                    ).addPropertyNode("startTime").addConstraintViolation();
                    isValid = false;
                }
            }
        }

        return isValid;
    }
}