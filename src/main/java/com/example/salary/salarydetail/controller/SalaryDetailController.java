package com.example.salary.salarydetail.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.salary.salarydetail.dto.SalaryDetailDto;
import com.example.salary.salarydetail.service.SalaryDetailService;

@Controller
public class SalaryDetailController {

    @Autowired
    private SalaryDetailService salaryDetailService;

    @GetMapping("/salary/detail")
    public String showDetail(int userId, int targetYear, int targetMonth, Model model) {

        // 給与詳細を取得
        SalaryDetailDto detail = salaryDetailService.getSalaryDetail(
                userId,
                targetYear,
                targetMonth
        );

        // 画面に渡す
        model.addAttribute("detail", detail);
        model.addAttribute("userId", userId);
        model.addAttribute("targetYear", targetYear);
        model.addAttribute("targetMonth", targetMonth);

        return "salaryDetail"; // salaryDetail.html
    }
}
