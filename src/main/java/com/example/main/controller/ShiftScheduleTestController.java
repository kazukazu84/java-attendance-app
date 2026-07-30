package com.example.main.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.main.entity.ShiftScheduleTest;
import com.example.main.service.ShiftScheduleTestService;

@Controller
public class ShiftScheduleTestController {

    @Autowired
    private ShiftScheduleTestService service;

    @GetMapping("/shift-test")
    public String shiftTest(
    		@RequestParam(required = false)
    		Integer week,
            Model model) {
    	
    	 boolean weeklyMode = (week != null);
    	
    	  if (week == null) {   
    		  week = 1;
    		  }

        LocalDate startDate;
        LocalDate endDate;

        switch (week) {

            case 2:
                startDate = LocalDate.of(2026, 7, 8);
                endDate = LocalDate.of(2026, 7, 14);
                break;

            case 3:
                startDate = LocalDate.of(2026, 7, 15);
                endDate = LocalDate.of(2026, 7, 21);
                break;

            case 4:
                startDate = LocalDate.of(2026, 7, 22);
                endDate = LocalDate.of(2026, 7, 28);
                break;

            case 5:
                startDate = LocalDate.of(2026, 7, 29);
                endDate = LocalDate.of(2026, 7, 31);
                break;

            default:
                startDate = LocalDate.of(2026, 7, 1);
                endDate = LocalDate.of(2026, 7, 7);
                break;
        }

        List<ShiftScheduleTest> monthlyShiftList =
                service.getAllShift();
        List<ShiftScheduleTest> weeklyShiftList =
                service.getShiftByWeek(
                        startDate,
                        endDate);
        model.addAttribute(
                "monthlyShiftList",
                monthlyShiftList);

        model.addAttribute(
                "weeklyShiftList",
                weeklyShiftList);
        
        model.addAttribute(
                "week",
                week);
       
        model.addAttribute(    
        		"weeklyMode",     
        		weeklyMode);
        
        return "shiftTest";
    }
    
}