package com.example.adminshift.status;

import java.time.LocalDate;

/**
 * シフト受付期間のステータスを表すEnum
 */
public enum ShiftApplicationEventStatus {

    BEFORE_APPLICATION("受付前"),
    DURING_APPLICATION("受付中"),
    AFTER_APPLICATION("受付終了");

    private final String label;

    ShiftApplicationEventStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /**
     * 現在日と受付期間を比較し、適切なステータスを判定して返します。
     *
     * @param applicationStartDate 受付開始日
     * @param applicationEndDate   受付終了日
     * @param now                  現在日
     * @return 該当するステータス
     */
    public static ShiftApplicationEventStatus of(LocalDate applicationStartDate, LocalDate applicationEndDate, LocalDate now) {
        if (applicationStartDate == null || applicationEndDate == null || now == null) {
            return BEFORE_APPLICATION;
        }

        if (now.isBefore(applicationStartDate)) {
            return BEFORE_APPLICATION;
        } else if (!now.isAfter(applicationEndDate)) {
            // 受付開始日 <= 現在日 <= 受付終了日
            return DURING_APPLICATION;
        } else {
            return AFTER_APPLICATION;
        }
    }
}