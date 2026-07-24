package com.example.adminshift.dto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GapInfo {

    private LocalDate startDate;
    private LocalDate endDate;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    /**
     * 要件通りのメッセージ形式を返却
     */
    public String getMessage() {
        if (startDate == null || endDate == null) {
            return "";
        }
        if (startDate.equals(endDate)) {
            return startDate.format(FORMATTER) + "のイベントが生成されていません";
        }
        return startDate.format(FORMATTER) + "～" + endDate.format(FORMATTER) + "のイベントが生成されていません";
    }
}