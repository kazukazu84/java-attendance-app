package com.example.adminshift.form;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class CreateShiftApplicationEventForm {

    /*
     * 対象期間（1～4週間）
     */
    @NotNull
    @Min(1)
    @Max(4)
    private Integer targetWeeks;

    /**
     * 受付開始（30～1日前）
     */
    @NotNull
    @Min(1)
    @Max(30)
    private Integer applicationStartDays;

    /**
     * 受付締切（14～1日前）
     */
    @NotNull
    @Min(1)
    @Max(14)
    private Integer applicationEndDays;

}