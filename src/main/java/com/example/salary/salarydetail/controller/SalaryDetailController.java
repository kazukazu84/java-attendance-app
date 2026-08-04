package com.example.salary.salarydetail.controller;

import java.time.Duration;
import java.time.LocalTime;
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
import org.springframework.web.bind.annotation.RequestParam;
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
            @RequestParam("userId") String userId,
            @RequestParam("targetYear") int targetYear,
            @RequestParam("targetMonth") int targetMonth,
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


        // ★ 給与情報（単一）
        SalaryDetailDto detail = salaryDetailService.getSalaryDetail(
                userId, targetYear, targetMonth
        );

        if (detail == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "該当データがありません");
            return "redirect:" + basePath + "/salary/confirm?userId=" + userId + "&targetYear=" + targetYear;
        }

        // ★ 当月の勤怠一覧（複数）

        // ★ 給与情報（同年同月は必ず1件）
        List<SalaryDetailDto> detailList;
        try {
            detailList = salaryDetailService.getSalaryDetailList(
                    userId, targetYear, targetMonth
            );
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "同年同月の給与データが複数存在します。修正が必要です。");
            return "redirect:" + basePath + "/salary/confirm?userId=" + userId + "&targetYear=" + targetYear;
        }

        if (detailList.isEmpty()) {

            // ★ null理由を取得
            String reason = salaryDetailService.getNullReason(userId, targetYear, targetMonth);

            redirectAttributes.addFlashAttribute("errorMessage", reason);

            return "redirect:" + basePath + "/salary/confirm?userId=" + userId + "&targetYear=" + targetYear;
        }


        // ★ 代表データ（1件のみ）
        SalaryDetailDto detail = detailList.get(0);

        // ★ 当月の勤怠一覧（表示用）

        List<Attendance> attendanceList =
                salaryDetailService.getAttendanceList(userId, targetYear, targetMonth)
                        .stream()
                        .sorted((a, b) -> b.getWorkDate().compareTo(a.getWorkDate())) // ★ 新しい日付 → 古い日付
                        .collect(Collectors.toList());


        // ★ 勤怠一覧を DTO に変換（Entity は触らない）

        // ★ 勤怠一覧を DTO に変換（表示用）

        List<AttendanceDto> attendanceDtoList = attendanceList.stream()
                .map(att -> {
                    AttendanceDto dto = new AttendanceDto();
                    dto.setWorkDate(att.getWorkDate());
                    dto.setClockIn(att.getClockIn() != null ? att.getClockIn().toString() : null);
                    dto.setClockOut(att.getClockOut() != null ? att.getClockOut().toString() : null);
                    dto.setRestTime(att.getRestTime());


                    // 勤務時間計算（Controller 側で完結）
                    if (att.getClockIn() != null && att.getClockOut() != null) {
                        double hours = Duration.between(att.getClockIn(), att.getClockOut())
                                .toMinutes() / 60.0;

                        // ★ rest_time（分）→ 時間に変換
                        double restHours = (att.getRestTime() == null ? 0 : att.getRestTime()) / 60.0;

                        dto.setWorkingHours(hours - restHours);
                    } else {
                        dto.setWorkingHours(0.0);
                    }

                    // ★ 表示用の勤務時間（給与計算には使わない）
                    if (att.getClockIn() != null && att.getClockOut() != null) {


                        LocalTime in = att.getClockIn().withSecond(0);
                        LocalTime out = att.getClockOut().withSecond(0);


        // ★ 勤務時間合計を計算（給与計算用）
        double totalWorkingHours = attendanceList.stream()
                .mapToDouble(att -> {

                    if (att.getClockIn() == null || att.getClockOut() == null) {
                        return 0.0; // ★ null の場合は 0 時間
                    }

                    double hours = Duration.between(att.getClockIn(), att.getClockOut())
                            .toMinutes() / 60.0;

                    // ★ rest_time（分）→ 時間に変換
                    Double restMinutes = att.getRestTime();
                    if (restMinutes == null) restMinutes = 0.0;

                        long minutes = Duration.between(in, out).toMinutes();

                        double restMinutes = (att.getRestTime() == null ? 0 : att.getRestTime());
                        minutes -= (long) restMinutes;


                        if (minutes < 0) minutes = 0;

                        double hours = Math.floor((minutes / 60.0) * 100) / 100.0;
                        dto.setWorkingHours(hours);

                    } else {
                        dto.setWorkingHours(0.0);
                    }


                    return result;


                    return dto;

                })
                .collect(Collectors.toList());



        detail.setWorkingHours(totalWorkingHours);

        // ★ detail の勤務時間は Service が計算済みの値をそのまま使う
        // Controller で再計算しない（仕様変更）


        // ★ 画面状態セット
        detail.setInitialDisplay(true);
        detail.setFromScreen("salaryConfirm");

        // ScreenState チェック
        Set<ConstraintViolation<SalaryDetailDto>> screenViolations =
                validator.validate(detail, ScreenStateGroup.class);

        if (!screenViolations.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "画面状態が不正です");
            return "redirect:" + basePath + "/salary/confirm?userId=" + userId + "&targetYear=" + targetYear;
        }

        // Consistency チェック
        Set<ConstraintViolation<SalaryDetailDto>> consistencyViolations =
                validator.validate(detail, ConsistencyGroup.class);

        if (!consistencyViolations.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "整合性が不正です");
            return "redirect:" + basePath + "/salary/confirm?userId=" + userId + "&targetYear=" + targetYear;
        }


        // ★ 画面へ渡す（DTO 化した勤怠一覧）


        // ★ 画面へ渡す（1件のみ）

        model.addAttribute("detail", detail);
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
