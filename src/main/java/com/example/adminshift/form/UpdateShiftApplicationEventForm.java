package com.example.adminshift.form;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.example.adminshift.validation.ValidShiftApplicationEventDate;

import lombok.Data;

@Data
@ValidShiftApplicationEventDate
public class UpdateShiftApplicationEventForm {
	
    private Integer eventId;

    /**
     * 対象期間開始日
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate targetStartDate;

    /**
     * 対象期間終了日
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate targetEndDate;

    /**
     * 受付開始日
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate applicationStartDate;

    /**
     * 受付終了日
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate applicationEndDate;

}