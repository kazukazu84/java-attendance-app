package com.example.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserMainController {
    // 👈 【追加】給与確認画面（仮）
    @GetMapping("/user/salary")
    public String salaryView() {
        return "tempSalary";
    }

    // 👈 【追加】シフト申請画面（仮）
    @GetMapping("/user/shift-request")
    public String shiftRequestView() {
        return "tempShiftRequest";
    }

}
