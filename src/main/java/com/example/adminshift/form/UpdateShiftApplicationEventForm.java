package com.example.adminshift.form;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat; // ★追加

import lombok.Data;

@Data
public class UpdateShiftApplicationEventForm {
	
    private Integer eventId;

    /**
     * 対象期間開始日
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) // ★追加
    private LocalDate targetStartDate;

    /**
     * 対象期間終了日
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) // ★追加
    private LocalDate targetEndDate;

    /**
     * 受付開始日
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) // ★追加
    private LocalDate applicationStartDate;

    /**
     * 受付終了日
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) // ★追加
    private LocalDate applicationEndDate;

}