package com.example.adminshift.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShiftRequestUserSummaryDto {
    private String userId;
    private String userName;
    private LocalDateTime submittedAt;

    public String getFormattedSubmittedAt() {
        if (submittedAt == null) {
            return "-";
        }
        return submittedAt.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
    }

    public String getStatus() {
        return submittedAt != null ? "提出" : "未提出";
    }

    public boolean isSubmitted() {
        return submittedAt != null;
    }
}