package com.example.main.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.account.entity.UserInfo;
import com.example.account.repository.UserInfoRepository;
import com.example.attendance.dto.AttendanceDto;
import com.example.attendance.service.AttendanceService;
import com.example.main.dto.LogDto;
import com.example.main.service.LogService;

@Controller
public class AdminMainController {

    @Autowired
    private LogService logService;

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private UserInfoRepository userRepository;

    /**
     * 管理者メイン画面の表示
     */
    @GetMapping("/admin/main")
    public String adminMainView(@AuthenticationPrincipal UserDetails loginUser, Model model) {
        if (loginUser == null) {
            return "redirect:/login";
        }

        String currentUserId = loginUser.getUsername();

        // 1. ユーザー情報の取得
        UserInfo currentUser = userRepository.findById(currentUserId).orElse(null);
        if (currentUser == null) {
            currentUser = new UserInfo();
            currentUser.setUserId(currentUserId);
            currentUser.setUserName("（名称未設定）");
        }
        model.addAttribute("user", currentUser);

        // 2. 勤怠ステータスの取得
        AttendanceDto attendanceDto = attendanceService.getStatus(currentUserId);
        model.addAttribute("status", attendanceDto);

        // 3. ログ一覧の取得
        List<LogDto> logList = logService.getLogListForMain(currentUserId);
        model.addAttribute("logList", logList);

        // 💡 ポイント: テンプレートは userMain.html をそのまま指定します！
        return "userMain";
    }
}