package com.example.userShiftRequest.dto;

public class ShiftRequestSelectDto {

    /** イベントID */
    private Integer eventId;

    /** 提出状態（済、未提出など） */
    private String submissionStatus;

    /** 対象期間（最終的に「LocalDate」を使用する） */
    private String targetPeriod;

    /** 締め日 （最終的に「LocalDate」を使用する）*/
    private String deadlineDate;

    /** 開始日 （最終的に「LocalDate」を使用する）*/
    private String startDate;

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

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
}