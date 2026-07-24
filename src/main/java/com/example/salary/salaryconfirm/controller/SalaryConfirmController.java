package com.example.salary.salaryconfirm.controller;

import java.util.List;
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

    /**
     * 給与一覧（GET）
     */
    @GetMapping({"/user/salary/confirm", "/admin/salary/confirm"})
    public String showConfirmForm(
            @AuthenticationPrincipal UserDetails loginUser,
            HttpServletRequest request,
            Model model) {

        if (loginUser == null) return "redirect:/login";

        // 権限に応じてURLを正規化
        String redirectUrl = checkAndRedirect(
                loginUser, request,
                "/user/salary/confirm", "/admin/salary/confirm"
        );
        if (redirectUrl != null) return redirectUrl;

        // ★ 権限に応じて basePath を画面へ渡す
        String basePath = loginUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))
                ? "/admin"
                : "/user";
        model.addAttribute("basePath", basePath);

        // ★ 初期表示フォーム（userId は String に変更）
        SalaryConfirmForm form = new SalaryConfirmForm();
        form.setUserId(loginUser.getUsername()); // ★ ログインユーザーのIDをセット

        model.addAttribute("salaryConfirmForm", form);
        model.addAttribute("yearList",
                salaryConfirmService.getAvailableYears(loginUser.getUsername()));


        return "salaryConfirm";
    }

    /**
     * 給与一覧（POST）
     */
    @PostMapping({"/user/salary/confirm", "/admin/salary/confirm"})
    public String confirmSalary(
            @AuthenticationPrincipal UserDetails loginUser,
            HttpServletRequest request,
            SalaryConfirmForm form,
            Model model) {

        if (loginUser == null) return "redirect:/login";

        String redirectUrl = checkAndRedirect(
                loginUser, request,
                "/user/salary/confirm", "/admin/salary/confirm"
        );
        if (redirectUrl != null) return redirectUrl;

        String basePath = loginUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))
                ? "/admin"
                : "/user";
        model.addAttribute("basePath", basePath);

        // ★ 年度未選択チェック（null → 500防止）
        if (form.getTargetYear() == null) {
            model.addAttribute("errorMessage", "年度を選択してください");
            model.addAttribute("salaryConfirmForm", form);
            model.addAttribute("yearList",
                    salaryConfirmService.getAvailableYears(loginUser.getUsername()));
            return "salaryConfirm";
        }

        String userId = form.getUserId();
        int targetYear = form.getTargetYear();
        Integer targetMonth = form.getTargetMonth();

        // ★ 年間差引支給額
        int totalNetSalary = salaryConfirmService.getTotalNetSalary(userId, targetYear);

        SalaryConfirmDto dto = new SalaryConfirmDto(
                targetMonth,
                totalNetSalary,
                userId,
                targetYear
        );

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

        // ★ 一覧データ取得
        List<SalaryConfirmDto> salaryList =
                salaryConfirmService.getSalaryList(userId, targetYear);

        // ★ 0件チェック（年度に給与データがない）
        if (salaryList.isEmpty()) {
            model.addAttribute("errorMessage", "該当年度の給与データがありません");
            model.addAttribute("salaryConfirmForm", form);
            model.addAttribute("yearList",
                    salaryConfirmService.getAvailableYears(loginUser.getUsername()));
            return "salaryConfirm";
        }

        // ★ 正常時のみ画面へ渡す
        model.addAttribute("salaryConfirmDto", dto);
        model.addAttribute("salaryList", salaryList);
        model.addAttribute("totalWorkingHours",
                (int) salaryConfirmService.getTotalWorkingHours(userId, targetYear));
        model.addAttribute("totalNetSalary", totalNetSalary);

        model.addAttribute("salaryConfirmForm", form);
        model.addAttribute("yearList",
                salaryConfirmService.getAvailableYears(loginUser.getUsername()));

        return "salaryConfirm";
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

        if (isAdmin && requestUri.endsWith(userPath)) {
            return "redirect:" + adminPath;
        }

        if (!isAdmin && requestUri.endsWith(adminPath)) {
            return "redirect:" + userPath;
        }

        return null;
    }
}
