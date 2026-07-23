package com.example.adminshift.userShiftRequest.select.service;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.adminshift.userShiftRequest.select.dto.ShiftRequestDto;
import com.example.adminshift.userShiftRequest.select.entity.ShiftRequestDetailsEntity;
import com.example.adminshift.userShiftRequest.select.form.ShiftRequestForm;
import com.example.adminshift.userShiftRequest.select.repository.ShiftRequestDetailsRepository;
import com.example.adminshift.userShiftRequest.select.validation.ShiftRequestValidator;

@Service
public class ShiftRequestService {
	
	@Autowired
	private ShiftRequestDetailsRepository repository;
	
	
	private final ShiftRequestValidator validator =
	        new ShiftRequestValidator();

    public ShiftRequestForm getShiftRequestInfo() {

        ShiftRequestForm form = new ShiftRequestForm();
        
        
        List<ShiftRequestDetailsEntity> entityList
        = repository.findAll();
        
        System.out.println(entityList.size());

        List<ShiftRequestDto> shiftList = new ArrayList<>();
        
        for (ShiftRequestDetailsEntity entity : entityList) {

            ShiftRequestDto dto = new ShiftRequestDto();

            dto.setWorkDate(
                    entity.getWorkDate().toString());

            dto.setAvailable(
                    entity.getIsAvailable() ? "○" : "×");

            dto.setStartTime(
                    entity.getRequestedStartTime().toString());

            dto.setEndTime(
                    entity.getRequestedEndTime().toString());

            shiftList.add(dto);
        }
        
        
        form.setTargetPeriod("12/22～12/29");

        form.setShiftList(shiftList);

        return form;
        
    }
    
    public boolean applyShiftRequest(ShiftRequestForm form) {
    	
    	boolean saved = false;

        for (ShiftRequestDto dto : form.getShiftList()) {
        	
        	if (!validator.isValid(dto)) {
        	    continue;
        	}	
        	
        	
            ShiftRequestDetailsEntity entity =
                    new ShiftRequestDetailsEntity();
            
            
            entity.setUserId(1);

            entity.setEventId(1);

            entity.setWorkDate(
                    Date.valueOf(dto.getWorkDate()));

            entity.setIsAvailable(
                    "○".equals(dto.getAvailable()));
            
            
            
            
            if ("○".equals(dto.getAvailable())) {

                entity.setRequestedStartTime(
                        Time.valueOf(dto.getStartTime()));

                entity.setRequestedEndTime(
                        Time.valueOf(dto.getEndTime()));

            } else {

                entity.setRequestedStartTime(null);

                entity.setRequestedEndTime(null);
            }
            repository.save(entity);
            
            saved = true;

            System.out.println("申請処理完了");
            
        }
        return saved;
    }
}