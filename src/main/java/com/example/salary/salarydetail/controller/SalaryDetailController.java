package com.example.salary.salarydetail.controller;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

import com.example.attendance.dto.AttendanceDto;
import com.example.attendance.entity.Attendance;
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

    @GetMapping({"/user/salary/detail", "/admin/salary/detail"})
    public String showDetail(
            @AuthenticationPrincipal UserDetails loginUser,
            HttpServletRequest request,
            String userId,
            int targetYear,
            int targetMonth,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (loginUser == null) return "redirect:/login";

        String redirectUrl = checkAndRedirect(
                loginUser, request,
                "/user/salary/detail", "/admin/salary/detail"
        );
        if (redirectUrl != null) return redirectUrl;

        String basePath = loginUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))
                ? "/admin"
                : "/user";
        model.addAttribute("basePath", basePath);

        // ★ 給与情報（複数）
        List<SalaryDetailDto> detailList = salaryDetailService.getSalaryDetailList(
                userId, targetYear, targetMonth
        );

        if (detailList.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "該当データがありません");
            return "redirect:" + basePath + "/salary/confirm?userId=" + userId + "&targetYear=" + targetYear;
        }

        // ★ 代表データ（従来の detail と同じ役割）
        SalaryDetailDto detail = detailList.get(0);

        // ★ 当月の勤怠一覧（複数）
        List<Attendance> attendanceList =
                salaryDetailService.getAttendanceList(userId, targetYear, targetMonth)
                        .stream()
                        .sorted((a, b) -> b.getWorkDate().compareTo(a.getWorkDate()))
                        .collect(Collectors.toList());

        // ★ 勤怠一覧を DTO に変換
        List<AttendanceDto> attendanceDtoList = attendanceList.stream()
                .map(att -> {
                    AttendanceDto dto = new AttendanceDto();
                    dto.setWorkDate(att.getWorkDate());
                    dto.setClockIn(att.getClockIn() != null ? att.getClockIn().toString() : null);
                    dto.setClockOut(att.getClockOut() != null ? att.getClockOut().toString() : null);
                    dto.setRestTime(att.getRestTime());

                    if (att.getClockIn() != null && att.getClockOut() != null) {
                        double hours = Duration.between(att.getClockIn(), att.getClockOut())
                                .toMinutes() / 60.0;

                        double restHours = (att.getRestTime() == null ? 0 : att.getRestTime()) / 60.0;

                        dto.setWorkingHours(hours - restHours);
                    } else {
                        dto.setWorkingHours(0.0);
                    }

                    return dto;
                })
                .collect(Collectors.toList());

        // ★ 勤務時間合計
        double totalWorkingHours = attendanceList.stream()
                .mapToDouble(att -> {
                    if (att.getClockIn() == null || att.getClockOut() == null) {
                        return 0.0;
                    }

                    double hours = Duration.between(att.getClockIn(), att.getClockOut())
                            .toMinutes() / 60.0;

                    Double restMinutes = att.getRestTime();
                    if (restMinutes == null) restMinutes = 0.0;

                    double restHours = restMinutes / 60.0;

                    double result = hours - restHours;

                    if (Double.isNaN(result) || Double.isInfinite(result)) {
                        return 0.0;
                    }

                    return result;
                })
                .sum();

        detail.setWorkingHours(totalWorkingHours);

        // ★ 画面状態セット（従来通り detail に対して）
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

        // ★ 画面へ渡す（複数件 + 代表データ）
        model.addAttribute("detail", detail);          // 従来の単一データ
        model.addAttribute("detailList", detailList);  // 新規追加：複数件
        model.addAttribute("attendanceList", attendanceDtoList);
        model.addAttribute("userId", userId);
        model.addAttribute("targetYear", targetYear);
        model.addAttribute("targetMonth", targetMonth);

        return "salaryDetail";
    }

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
