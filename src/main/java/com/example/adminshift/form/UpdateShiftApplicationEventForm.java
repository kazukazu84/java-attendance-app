package com.example.adminshift.form;

import java.time.LocalDate;

import com.example.adminshift.validation.ValidShiftApplicationEventDate;
import com.example.adminshift.validation.ValidShiftTargetDate; // ★追加

import lombok.Data;

@Data
@ValidShiftApplicationEventDate // 受付期間のチェック（受付開始日 <= 受付終了日）
@ValidShiftTargetDate            // 対象期間のチェック（対象開始日 <= 対象終了日）★追加
public class UpdateShiftApplicationEventForm implements ShiftApplicationDateHolder {

    private Integer eventId;

    private LocalDate targetStartDate;

    private LocalDate targetEndDate;

    private LocalDate applicationStartDate;

    private LocalDate applicationEndDate;
}