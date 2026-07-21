package com.example.adminshift.userShiftRequest.select.form;

import java.util.List;

import com.example.adminshift.userShiftRequest.select.dto.ShiftRequestDto;

public class ShiftRequestForm {

	
	/* 対象期間 */
    private String targetPeriod;
    
    private List<ShiftRequestDto> shiftList;


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
}