package com.example.userShiftRequest.form;


import java.util.List;

import com.example.userShiftRequest.dto.ShiftSelectDto;

/**
 * シフト申請選択画面用フォーム
 */
public class ShiftSelectForm {

    private int selectedYear;
    private List<ShiftSelectDto> shiftList;

    // ゲッター・セッター
    public int getSelectedYear() {
        return selectedYear;
    }

    public void setSelectedYear(int selectedYear) {
        this.selectedYear = selectedYear;
    }

    public List<ShiftSelectDto> getShiftList() {
        return shiftList;
    }

    public void setShiftList(List<ShiftSelectDto> shiftList) {
        this.shiftList = shiftList;
    }
}