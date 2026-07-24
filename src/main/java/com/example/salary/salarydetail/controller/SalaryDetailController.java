package com.example.salary.salarydetail.controller;

import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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

    /**
     * 給与詳細（GET）
     */
    @GetMapping({"/user/salary/detail", "/admin/salary/detail"})
    public String showDetail(
            @AuthenticationPrincipal UserDetails loginUser,
            HttpServletRequest request,
            String userId,          // ★ String に変更
            int targetYear,
            int targetMonth,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (loginUser == null) return "redirect:/login";

        // 権限に応じてURLを正規化
        String redirectUrl = checkAndRedirect(
                loginUser, request,
                "/user/salary/detail", "/admin/salary/detail"
        );
        if (redirectUrl != null) return redirectUrl;

        // ★ 権限に応じて basePath を画面へ渡す
        String basePath = loginUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))
                ? "/admin"
                : "/user";
        model.addAttribute("basePath", basePath);

        // ★ 最新仕様の Repository メソッドに合わせる
        SalaryDetailDto detail = salaryDetailService.getSalaryDetail(
                userId,
                targetYear,
                targetMonth
        );

        // データが無い場合は一覧へ戻す
        if (detail == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "該当データがありません");
            return "redirect:" + basePath + "/salary/confirm?userId=" + userId + "&targetYear=" + targetYear;
        }

        // 画面状態セット
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

    /**
     * MainController と同じ権限判定ロジック
     */
    private String checkAndRedirect(
            UserDetails loginUser,
            HttpServletRequest request,
            String userPath,
            String adminPath) {

        boolean isAdmin = loginUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        String requestUri = request.getRequestURI();

        // 管理者が /user/... にアクセスした場合
        if (isAdmin && requestUri.endsWith(userPath)) {
            return "redirect:" + adminPath;
        }

        // 一般ユーザーが /admin/... にアクセスした場合
        if (!isAdmin && requestUri.endsWith(adminPath)) {
            return "redirect:" + userPath;
        }

        return null;
    }
}
