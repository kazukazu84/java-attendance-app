package com.example.adminshift.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShiftRequestDetailDto {
    private LocalDate workDate;
    private Boolean isAvailable;
    private LocalTime requestedStartTime;
    private LocalTime requestedEndTime;

    public String getFormattedWorkDate() {
        if (workDate == null) return "";
        return workDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }

    public String getAvailabilityText() {
        return Boolean.TRUE.equals(isAvailable) ? "〇" : "×";
    }

    public String getStartTimeText() {
        if (Boolean.TRUE.equals(isAvailable) && requestedStartTime != null) {
            return requestedStartTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        }
        return "-";
    }

    public String getEndTimeText() {
        if (Boolean.TRUE.equals(isAvailable) && requestedEndTime != null) {
            return requestedEndTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        }
        return "-";
    }
}