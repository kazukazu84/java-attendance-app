package com.example.adminshift.userShiftRequest.select.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.adminshift.userShiftRequest.select.form.ShiftRequestForm;
import com.example.adminshift.userShiftRequest.select.service.ShiftRequestService;

@Controller
public class ShiftRequestController {
	
	@Autowired
	private ShiftRequestService shiftRequestService;

    /**
     * シフト申請画面表示
     */
    @GetMapping("/shiftRequest")
    public String showShiftRequest(Model model) {
    	

    	// フォーム生成
        ShiftRequestForm form = shiftRequestService.getShiftRequestInfo();
        
     // 画面へ渡す
        model.addAttribute("form", form);

     // 画面表示
        return "shiftRequest";

    }

}