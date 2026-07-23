package com.example.salary.salarydetail.controller;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.salary.common.validation.ConsistencyGroup;
import com.example.salary.common.validation.ScreenStateGroup;
import com.example.salary.salarydetail.dto.SalaryDetailDto;
import com.example.salary.salarydetail.service.SalaryDetailService;

@Controller
public class SalaryDetailController {

    @Autowired
    private SalaryDetailService salaryDetailService;

    @Autowired
    private Validator validator;

    @GetMapping("/salary/detail")
    public String showDetail(int userId, int targetYear, int targetMonth,
                             Model model,
                             RedirectAttributes redirectAttributes) {

        SalaryDetailDto detail = salaryDetailService.getSalaryDetail(
                userId,
                targetYear,
                targetMonth
        );

        // ★ 追加：データが無い場合は選択ページへ戻す（PRG）
        if (detail == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "該当データがありません");
            return "redirect:/salary/confirm?userId=" + userId + "&targetYear=" + targetYear;
        }

        // Controller が画面状態をセット
        detail.setInitialDisplay(true);
        detail.setFromScreen("salaryConfirm");

        // ScreenState チェック
        Set<ConstraintViolation<SalaryDetailDto>> screenViolations =
                validator.validate(detail, ScreenStateGroup.class);

        if (!screenViolations.isEmpty()) {
            model.addAttribute("error", "画面状態が不正です");
            return "salaryDetail";
        }

        // Consistency チェック
        Set<ConstraintViolation<SalaryDetailDto>> consistencyViolations =
                validator.validate(detail, ConsistencyGroup.class);

        if (!consistencyViolations.isEmpty()) {
            model.addAttribute("error", "整合性が不正です");
            return "salaryDetail";
        }

        model.addAttribute("detail", detail);
        model.addAttribute("userId", userId);
        model.addAttribute("targetYear", targetYear);
        model.addAttribute("targetMonth", targetMonth);

        return "salaryDetail";
    }

}