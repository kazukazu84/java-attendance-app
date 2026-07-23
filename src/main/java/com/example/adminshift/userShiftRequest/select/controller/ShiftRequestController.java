package com.example.adminshift.userShiftRequest.select.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.adminshift.userShiftRequest.select.dto.ShiftRequestDto;
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
    
    @PostMapping("/shiftRequest/apply")
    public String applyShiftRequest
    (ShiftRequestForm form, RedirectAttributes redirectAttributes) {
    	
    	for (ShiftRequestDto dto : form.getShiftList()) {

    	    System.out.println("日付=" + dto.getWorkDate());

    	    System.out.println("可否=" + dto.getAvailable());

    	    System.out.println("出勤=" + dto.getStartTime());

    	    System.out.println("退勤=" + dto.getEndTime());
    	}
    	
        boolean result = 
        		shiftRequestService.applyShiftRequest(form);
        
        if(result) {
        	
        	redirectAttributes.addFlashAttribute
        	("message", "申請が完了しました。");
            
        } else {
        	redirectAttributes.addFlashAttribute
        	("message", "入力内容を確認してください。");
        }
        
        return "redirect:/shiftRequest";
    }

}