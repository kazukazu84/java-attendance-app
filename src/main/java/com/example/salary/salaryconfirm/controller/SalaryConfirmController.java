package com.example.salary.salaryconfirm.controller;

import java.util.List;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.salary.common.validation.ConsistencyGroup;
import com.example.salary.common.validation.ScreenStateGroup;
import com.example.salary.salaryconfirm.dto.SalaryConfirmDto;
import com.example.salary.salaryconfirm.form.SalaryConfirmForm;
import com.example.salary.salaryconfirm.service.SalaryConfirmService;

@Controller
public class SalaryConfirmController {

    @Autowired
    private SalaryConfirmService salaryConfirmService;

    @Autowired
    private Validator validator;

    @GetMapping("/salary/confirm")
    public String showConfirmForm(Model model) {

        SalaryConfirmForm form = new SalaryConfirmForm();
        form.setUserId(1);

        model.addAttribute("salaryConfirmForm", form);
        model.addAttribute("yearList", List.of(2024, 2025, 2026));

        return "salaryConfirm";
    }

    @PostMapping("/salary/confirm")
    public String confirmSalary(SalaryConfirmForm form, Model model) {

        int userId = form.getUserId();
        int targetYear = form.getTargetYear();
        Integer targetMonth = form.getTargetMonth();

        SalaryConfirmDto dto = new SalaryConfirmDto(
                targetMonth,
                (int) salaryConfirmService.getTotalNetSalary(userId, targetYear),
                userId,
                targetYear
        );

        // Controller が画面状態をセット
        dto.setInitialDisplay(true);
        dto.setFromScreen("main");

        // ScreenState チェック
        Set<ConstraintViolation<SalaryConfirmDto>> screenViolations =
                validator.validate(dto, ScreenStateGroup.class);

        if (!screenViolations.isEmpty()) {
            model.addAttribute("error", "画面状態が不正です");
            return "salaryConfirm";
        }

        // Consistency チェック
        Set<ConstraintViolation<SalaryConfirmDto>> consistencyViolations =
                validator.validate(dto, ConsistencyGroup.class);

        if (!consistencyViolations.isEmpty()) {
            model.addAttribute("error", "整合性が不正です");
            return "salaryConfirm";
        }

        model.addAttribute("salaryConfirmDto", dto);
        model.addAttribute("salaryList",
                salaryConfirmService.getSalaryList(userId, targetYear));

        model.addAttribute("totalWorkingHours",
                (int) salaryConfirmService.getTotalWorkingHours(userId, targetYear));

        model.addAttribute("totalNetSalary",
                (int) salaryConfirmService.getTotalNetSalary(userId, targetYear));

        model.addAttribute("salaryConfirmForm", form);
        model.addAttribute("yearList", List.of(2024, 2025, 2026));

        return "salaryConfirm";
    }
}