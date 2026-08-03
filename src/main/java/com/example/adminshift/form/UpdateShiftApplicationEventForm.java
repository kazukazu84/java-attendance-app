package com.example.adminshift.form;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

import com.example.adminshift.validation.ValidShiftApplicationEventDate;
import com.example.adminshift.validation.ValidShiftTargetDate;

import lombok.Data;

@Data
@ValidShiftApplicationEventDate // 受付期間のチェック（受付開始日 <= 受付終了日）
@ValidShiftTargetDate            // 対象期間のチェック（対象開始日 <= 対象終了日）
public class UpdateShiftApplicationEventForm implements ShiftApplicationDateHolder {

    private Integer eventId;

    @NotNull(message = "対象期間開始日を入力してください。")
    private LocalDate targetStartDate;

    @NotNull(message = "対象期間終了日を入力してください。")
    private LocalDate targetEndDate;

    @NotNull(message = "受付開始日を入力してください。")
    private LocalDate applicationStartDate;

    @NotNull(message = "受付終了日を入力してください。")
    private LocalDate applicationEndDate;
}