package com.example.adminshift.userShiftRequest.select.entity;

import java.sql.Date;
import java.sql.Time;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "shift_request_details")
public class ShiftRequestDetailsEntity {

    /** 希望ID */
	@Id
    private Integer requestDetailId;

    /** ユーザーID */
    private Integer userId;

    /** イベントID */
    private Integer eventId;

    /** 勤務日 */
    private Date workDate;

    /** 勤務可否 */
    private Boolean isAvailable;

    /** 希望開始時刻 */
    private Time requestedStartTime;

    /** 希望終了時刻 */
    private Time requestedEndTime;

	public Integer getRequestDetailId() {
		return requestDetailId;
	}

	public void setRequestDetailId(Integer requestDetailId) {
		this.requestDetailId = requestDetailId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getEventId() {
		return eventId;
	}

	public void setEventId(Integer eventId) {
		this.eventId = eventId;
	}

	public Date getWorkDate() {
		return workDate;
	}

	public void setWorkDate(Date workDate) {
		this.workDate = workDate;
	}

	public Boolean getIsAvailable() {
		return isAvailable;
	}

	public void setIsAvailable(Boolean isAvailable) {
		this.isAvailable = isAvailable;
	}

	public Time getRequestedStartTime() {
		return requestedStartTime;
	}

	public void setRequestedStartTime(Time requestedStartTime) {
		this.requestedStartTime = requestedStartTime;
	}

	public Time getRequestedEndTime() {
		return requestedEndTime;
	}

	public void setRequestedEndTime(Time requestedEndTime) {
		this.requestedEndTime = requestedEndTime;
	}

}