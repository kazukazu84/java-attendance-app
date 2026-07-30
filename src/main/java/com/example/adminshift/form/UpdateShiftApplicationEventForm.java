package com.example.adminshift.form;

import java.time.LocalDate;

import com.example.adminshift.validation.ValidShiftApplicationEventDate;

import lombok.Data;

@Data
@ValidShiftApplicationEventDate
public class UpdateShiftApplicationEventForm implements ShiftApplicationDateHolder {

    private Integer eventId;

    private LocalDate targetStartDate;

    private LocalDate targetEndDate;

    private LocalDate applicationStartDate;

    private LocalDate applicationEndDate;
}