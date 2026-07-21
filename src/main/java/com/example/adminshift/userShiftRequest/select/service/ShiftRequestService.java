package com.example.adminshift.userShiftRequest.select.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.adminshift.userShiftRequest.select.dto.ShiftRequestDto;
import com.example.adminshift.userShiftRequest.select.form.ShiftRequestForm;
import com.example.adminshift.userShiftRequest.select.repository.ShiftRequestDetailsRepository;

@Service
public class ShiftRequestService {
	
	@Autowired
	private ShiftRequestDetailsRepository repository;

    public ShiftRequestForm getShiftRequestInfo() {

        ShiftRequestForm form = new ShiftRequestForm();

        List<ShiftRequestDto> shiftList = new ArrayList<>();

        ShiftRequestDto shift1 = new ShiftRequestDto();
        shift1.setWorkDate("12/22");
        shift1.setAvailable("○");
        shift1.setStartTime("22:00");
        shift1.setEndTime("07:00");

        shiftList.add(shift1);

        ShiftRequestDto shift2 = new ShiftRequestDto();
        shift2.setWorkDate("12/24");
        shift2.setAvailable("○");
        shift2.setStartTime("22:30");
        shift2.setEndTime("02:30");

        shiftList.add(shift2);

        form.setTargetPeriod("12/22～12/29");
        form.setShiftList(shiftList);

        return form;
        
    }
}