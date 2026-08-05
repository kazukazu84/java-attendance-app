package com.example.userShiftRequest.dto;

/**
 * シフト申請選択画面の一覧表示用DTO
 */
public class ShiftSelectDto {

    private Integer eventId;
    private String submissionStatus; // 提出 / 未提出
    private String targetPeriod;     // 対象期間
    private String deadlineDate;     // 締め日

    // --- ゲッター・セッター ---
    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public String getSubmissionStatus() {
        return submissionStatus;
    }

    public void setSubmissionStatus(String submissionStatus) {
        this.submissionStatus = submissionStatus;
    }

    public String getTargetPeriod() {
        return targetPeriod;
    }

    public void setTargetPeriod(String targetPeriod) {
        this.targetPeriod = targetPeriod;
    }

    public String getDeadlineDate() {
        return deadlineDate;
    }

    public void setDeadlineDate(String deadlineDate) {
        this.deadlineDate = deadlineDate;
    }
}