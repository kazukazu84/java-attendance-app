package com.example.attendance.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceDto {

    @JsonProperty("statusMessage")
    private String statusMessage;

    @JsonProperty("canClockIn")
    private boolean canClockIn;

    @JsonProperty("canClockOut")
    private boolean canClockOut;

    // ★ 給与計算に必須（退勤した日の年月を渡す）
    @JsonProperty("workDate")
    private LocalDate workDate;
}
