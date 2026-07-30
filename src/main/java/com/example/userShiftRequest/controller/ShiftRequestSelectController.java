package com.example.userShiftRequest.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.userShiftRequest.form.ShiftRequestSelectForm;
import com.example.userShiftRequest.service.ShiftRequestSelectService;


// シフト申請選択画面

@Controller
public class ShiftRequestSelectController {

    @Autowired
    private ShiftRequestSelectService shiftRequestSelectService;

    
    @GetMapping("/shiftRequestSelect")
    public String showShiftRequestSelect
    (@RequestParam(required = false)
    Integer selectedYear , Model model) {
    	
    	if (selectedYear == null) {
    			selectedYear = LocalDate.now().getYear();
    	}
    	
    	// ↓動作確認用ログ
    	System.out.println("選択年度 = " + selectedYear);
    	
        ShiftRequestSelectForm form =
                shiftRequestSelectService.getShiftList(selectedYear);
        
        form.setSelectedYear(selectedYear);

        model.addAttribute("form", form);

        return "shiftRequestSelect";
    }
}