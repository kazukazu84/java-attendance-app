package com.example.userShiftRequest.dto;

public class ShiftRequestDto {

    /** 日付 */
    private String workDate;

    /** 出勤可否 */
    private String available;

    /** 出勤時間 */
    private String startTime;

    /** 退勤時間 */
    private String endTime;

    public String getWorkDate() {
        return workDate;
    }

    public void setWorkDate(String workDate) {
        this.workDate = workDate;
    }

    public String getAvailable() {
        return available;
    }

    public void setAvailable(String available) {
        this.available = available;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}