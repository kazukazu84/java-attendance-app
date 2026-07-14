package com.example.attendance.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.attendance.dto.AttendanceDto;
import com.example.attendance.service.AttendanceService;

@Controller
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    // テスト用のユーザーID (本来はセッション等から取得)
    private final Integer TEST_USER_ID = 1;

    // 初期画面表示
    @GetMapping("/user/attendance") // /user/indexから変更
    public String attendance(Model model) {
        // 初期表示用のステータスを取得してThymeleafに渡す
        AttendanceDto dto = attendanceService.getStatus(TEST_USER_ID);
        model.addAttribute("status", dto);
        return "attendance";
    }

    // 出勤処理（API）
    @PostMapping("/api/attendance/clock-in")
    @ResponseBody
    public AttendanceDto clockIn() {
        return attendanceService.clockIn(TEST_USER_ID);//★
    }

    // 退勤処理（API）
    @PostMapping("/api/attendance/clock-out")
    @ResponseBody
    public AttendanceDto clockOut() {
        return attendanceService.clockOut(TEST_USER_ID);//★
    }
}