package com.example.userShiftRequest.form;

import java.util.List;

import com.example.userShiftRequest.dto.ShiftRequestSelectDto;

public class ShiftRequestSelectForm {

    /** 選択年 */
    private Integer selectedYear;

    /** シフト一覧 */
    private List<ShiftRequestSelectDto> shiftList;

    public Integer getSelectedYear() {
        return selectedYear;
    }

    public void setSelectedYear(Integer selectedYear) {
        this.selectedYear = selectedYear;
    }

    public List<ShiftRequestSelectDto> getShiftList() {
        return shiftList;
    }

    public void setShiftList(List<ShiftRequestSelectDto> shiftList) {
        this.shiftList = shiftList;
    }
}