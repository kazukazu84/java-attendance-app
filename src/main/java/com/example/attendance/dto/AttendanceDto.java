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

    @JsonProperty("workDate")
    private LocalDate workDate;

    // ★ 給与詳細画面用：勤務時間（Controller で計算して詰める）
    @JsonProperty("workingHours")
    private Double workingHours;

    // ★ 給与詳細画面用：出勤・退勤時刻
    @JsonProperty("clockIn")
    private String clockIn;

    @JsonProperty("clockOut")
    private String clockOut;

    @JsonProperty("restTime")
    private Double restTime;

    // ★ 旧仕様の 4 引数コンストラクター（既存コード用）
    public AttendanceDto(String statusMessage, boolean canClockIn, boolean canClockOut, LocalDate workDate) {
        this.statusMessage = statusMessage;
        this.canClockIn = canClockIn;
        this.canClockOut = canClockOut;
        this.workDate = workDate;
    }
}
