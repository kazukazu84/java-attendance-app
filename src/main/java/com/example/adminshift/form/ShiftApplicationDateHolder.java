package com.example.adminshift.form;

import java.time.LocalDate;

/**
 * シフト受付期間の日付検証を行うForm用共通インターフェース
 */
public interface ShiftApplicationDateHolder {

    /**
     * 検証対象の受付開始日（または計算後の受付開始日）を取得します。
     */
    LocalDate getApplicationStartDate();

    /**
     * 検証対象の受付終了日（または計算後の受付終了日）を取得します。
     */
    LocalDate getApplicationEndDate();
}