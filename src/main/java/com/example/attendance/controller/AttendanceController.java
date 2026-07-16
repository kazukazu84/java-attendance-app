package com.example.attendance.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.attendance.dto.AttendanceDto;
import com.example.attendance.service.AttendanceService;
import com.example.main.service.LogService;

@Controller
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;
    
	// 👇 1. LogServiceを注入
    @Autowired
    private LogService logService;

    // テスト用のユーザーID (本来はセッション等から取得)
    private final String TEST_USER_ID = "1";

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
//    	// 1. 打刻処理を実行
//        AttendanceDto dto = attendanceService.clockIn(TEST_USER_ID);
//        
//        // 2. ⭐追加：ログ保存を呼び出す（例：メッセージマスタのID「1」を出勤ログと仮定）
//        logService.saveLog(1, TEST_USER_ID);
//        
//        return dto;
    }

    // 退勤処理（API）
    @PostMapping("/api/attendance/clock-out")
    @ResponseBody
    public AttendanceDto clockOut() {
        return attendanceService.clockOut(TEST_USER_ID);//★
//    	// 1. 打刻処理を実行
//        AttendanceDto dto = attendanceService.clockOut(TEST_USER_ID);
//        
//        // 2. ⭐追加：ログ保存を呼び出す（例：メッセージマスタのID「2」を退勤ログと仮定）
//        logService.saveLog(2, TEST_USER_ID);
//        
//        return dto;
    }
}