package com.example.adminshift.form;

import jakarta.validation.constraints.NotNull;

import lombok.Data;

/**
 * シフト表表示時のイベント選択用フォームデータ
 */
@Data
public class ShiftSearchForm {

    /** 選択されたイベントID */
    @NotNull(message = "イベントを選択してください")
    private Integer selectedEventId;
}