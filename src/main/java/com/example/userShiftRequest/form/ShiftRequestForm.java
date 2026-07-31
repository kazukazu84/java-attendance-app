package com.example.userShiftRequest.form;

import java.util.List;

import com.example.userShiftRequest.dto.ShiftRequestDto;

public class ShiftRequestForm {

	
	/* 対象期間 */
    
    private String targetPeriod;
    
    private List<ShiftRequestDto> shiftList;
    
    private Integer eventId;
    
    private Integer selectedYear;
    
    private String workDate;
    
    private String available;
    
    private String startTime;
    
    private String endTime;


    
	public String getWorkDate() {
		return workDate;
	}

	public void setWorkDate(String workDate) {
		this.workDate = workDate;
	}

	public String getTargetPeriod() {
        return targetPeriod;
    }
    
    public void setTargetPeriod(String targetPeriod) {
        this.targetPeriod = targetPeriod;
    }
    
    
    public List<ShiftRequestDto> getShiftList(){
    	return shiftList;
    }
    
    public void setShiftList(List<ShiftRequestDto> shiftList) {
    	this.shiftList = shiftList;
    }
    
    public Integer getEventId() {
    	return eventId;
    }
    
    public void setEventId(Integer eventId) {
    	this.eventId = eventId;
    }
    
    
    /* （7/22追加分　シフト申請の入力欄） */
    
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
	
	public Integer getSelectedYear() {
	    return selectedYear;
	}

	public void setSelectedYear(Integer selectedYear) {
	    this.selectedYear = selectedYear;
	}
}