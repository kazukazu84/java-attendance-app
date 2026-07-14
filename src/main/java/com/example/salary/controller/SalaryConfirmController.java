package com.example.salary.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class SalaryConfirmController {
	
	
	/* 初期表示 */
	
	@GetMapping("/salaryConfirm")
	public String showSalaryConfirm() {
		
		return "salaryConfirm";
	}
	
	
	
	/* 年選択 */
	
	@PostMapping("/salaryConfirm/search")
	public String searchByYear() {
		
		return "salaryConfirm";
	}
	
	
	
	/* 詳細画面へ遷移 */
	
	@GetMapping("/salaryDetail")
	public String moveSalaryDetail() {
		
		return "salaryDetail";
	}
	
	
	
}
