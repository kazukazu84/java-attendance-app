package com.example.adminshift.form;

import java.time.LocalDate;

import lombok.Data;

@Data
public class UpdateShiftApplicationEventForm {
	
	/**
     * イベントID
     */
    private Integer eventId;

    /**
     * 対象期間開始日
     */
    private LocalDate targetStartDate;

    /**
     * 対象期間終了日
     */
    private LocalDate targetEndDate;

    /**
     * 受付開始日
     */
    private LocalDate applicationStartDate;

    /**
     * 受付終了日
     */
    private LocalDate applicationEndDate;

   

}
