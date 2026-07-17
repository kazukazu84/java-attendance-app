package com.example.salary.salaryconfirm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.salary.salaryconfirm.form.SalaryConfirmForm;
import com.example.salary.salaryconfirm.service.SalaryConfirmService;

@Controller
public class SalaryConfirmController {

    @Autowired
    private SalaryConfirmService salaryConfirmService;

    @GetMapping("/salary/confirm")
    public String showConfirmForm(Model model) {

        SalaryConfirmForm form = new SalaryConfirmForm();
        form.setUserId(1);        // テスト用固定値
        form.setTargetYear(2026); // テスト用固定値

        model.addAttribute("salaryConfirmForm", form);
        model.addAttribute("yearList", List.of(2024, 2025, 2026));

        return "salaryConfirm";
    }

    @PostMapping("/salary/confirm")
    public String confirmSalary(SalaryConfirmForm form, Model model) {

        int userId = form.getUserId();
        int targetYear = form.getTargetYear();

        model.addAttribute("salaryList",
                salaryConfirmService.getSalaryList(userId, targetYear));

        model.addAttribute("totalWorkingHours",
                salaryConfirmService.getTotalWorkingHours(userId, targetYear));

        model.addAttribute("totalNetSalary",
                salaryConfirmService.getTotalNetSalary(userId, targetYear));

        model.addAttribute("salaryConfirmForm", form);
        model.addAttribute("yearList", List.of(2024, 2025, 2026));

        return "salaryConfirm";
    }

}
