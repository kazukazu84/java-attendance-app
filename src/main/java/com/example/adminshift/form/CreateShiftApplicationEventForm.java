package com.example.adminshift.form;

import java.time.LocalDate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import com.example.adminshift.validation.ValidShiftApplicationEventDate;

import lombok.Data;

@Data
@ValidShiftApplicationEventDate
public class CreateShiftApplicationEventForm implements ShiftApplicationDateHolder {

    @NotNull
    @Min(1)
    @Max(4)
    private Integer targetWeeks;

    @NotNull
    @Min(1)
    @Max(30)
    private Integer applicationStartDays;

    @NotNull
    @Min(1)
    @Max(14)
    private Integer applicationEndDays;

    /**
     * 相関チェック用：基準日（本日など）からの計算結果または日数の比較用判定
     * ※ 「何日前」という概念のため、日数で比較する場合は
     *    applicationStartDays (例:30日前) > applicationEndDays (例:14日前) である必要があります。
     *    ここでは基準日（本日）から算出した仮想の日付を返してValidatorで統一比較します。
     */
    @Override
    public LocalDate getApplicationStartDate() {
        if (applicationStartDays == null) {
            return null;
        }
        // 例: 本日を基準日として「N日前」の日付を算出
        return LocalDate.now().minusDays(applicationStartDays);
    }

    @Override
    public LocalDate getApplicationEndDate() {
        if (applicationEndDays == null) {
            return null;
        }
        return LocalDate.now().minusDays(applicationEndDays);
    }
}