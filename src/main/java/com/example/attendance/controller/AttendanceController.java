package com.example.attendance.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.attendance.dto.AttendanceDto;
import com.example.attendance.service.AttendanceService;
import com.example.main.service.LogService;

import jakarta.servlet.http.HttpServletRequest; // 👈 1. importを追加

@Controller
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;
    
	// 👇 1. LogServiceを注入
    @Autowired
    private LogService logService;

    /**
     * 簡易勤怠画面の初期表示
     */
    @GetMapping({"/user/attendance", "/admin/attendance"})
    public String attendance(@AuthenticationPrincipal UserDetails loginUser, 
                             HttpServletRequest request, // 👈 2. 引数に request を追加
                             Model model) {
        if (loginUser == null) {
            return "redirect:/login";
        }

        // 権限チェック：管理者の場合は /admin/attendance へ強制リダイレクト
        boolean isAdmin = loginUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // 管理者が /user/attendance にアクセスした場合は /admin/attendance へ転送
        if (isAdmin && request.getRequestURI().endsWith("/user/attendance")) {
            return "redirect:/admin/attendance";
        }

        String currentUserId = loginUser.getUsername();

        // ログインユーザーのステータスを取得してThymeleafに渡す
        AttendanceDto dto = attendanceService.getStatus(currentUserId);
        model.addAttribute("status", dto);
        return "attendance";
    }

    // 出勤処理（API）
    @PostMapping("/api/attendance/clock-in")
    @ResponseBody
    public AttendanceDto clockIn(@AuthenticationPrincipal UserDetails loginUser) {
        if (loginUser == null) {
            throw new RuntimeException("ログインセッションが切れています。再ログインしてください。");
    }

        String currentUserId = loginUser.getUsername();

        // 打刻処理（※ Service 内で logService.saveLog(0, currentUserId) が実行されます）
        return attendanceService.clockIn(currentUserId);
    }

    /**
     * 退勤処理（API）
     */
    @PostMapping("/api/attendance/clock-out")
    @ResponseBody
    public AttendanceDto clockOut(@AuthenticationPrincipal UserDetails loginUser) {
        if (loginUser == null) {
            throw new RuntimeException("ログインセッションが切れています。再ログインしてください。");
        }

        String currentUserId = loginUser.getUsername();

        // 打刻処理（※ Service 内で logService.saveLog(1, currentUserId) が実行されます）
        return attendanceService.clockOut(currentUserId);
    }
}