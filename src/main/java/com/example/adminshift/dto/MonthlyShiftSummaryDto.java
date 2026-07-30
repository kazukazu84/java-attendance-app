package com.example.adminshift.dto;

import java.time.YearMonth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ユーザーごとの月間シフト集計情報
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyShiftSummaryDto {

    /**
     * 集計対象年月
     */
    private YearMonth yearMonth;

    /**
     * 勤務日数
     */
    private int workingDays;

    /**
     * 勤務時間（分）
     */
    private long totalMinutes;

    /**
     * 表示用月
     *
     * 例：8月
     */
    public String getMonthText() {
        return yearMonth.getMonthValue() + "月";
    }

    /**
     * 表示用勤務時間
     *
     * 例：
     * 160時間30分
     */
    public String getTotalTimeText() {

        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;

        return hours + "時間" + minutes + "分";
    }

    /**
     * 勤務日数表示
     */
    public String getWorkingDaysText() {
        return workingDays + "日";
    }
}