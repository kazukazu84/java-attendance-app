package com.example.attendance.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.attendance.dto.AttendanceDto;
import com.example.attendance.entity.TempUserInfo;
import com.example.attendance.repository.TempUserInfoRepository;
//import com.example.attendance.dto.LogDto;
//import com.example.attendance.repository.UserRepository;
import com.example.attendance.service.AttendanceService;
import com.example.main.dto.LogDto;
//import com.example.attendance.service.LogService;
import com.example.main.service.LogService;

@Controller
public class MainController { // 👈 クラス名を「MainController」に変更

    @Autowired
    private LogService logService;

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private TempUserInfoRepository userRepository;

    private final Integer TEST_USER_ID = 1;

    /**
     * メイン画面の表示（URLと名前が完全に一致して分かりやすくなります）
     */
    @GetMapping("/user/main")
    public String mainView(Model model) {
        // 中身の処理は1行も変えずにそのままでOKです
        TempUserInfo currentUser = userRepository.findById(TEST_USER_ID).orElse(null);
        if (currentUser == null) {
            currentUser = new TempUserInfo();
            currentUser.setUserId(TEST_USER_ID);
            currentUser.setUserName("テスト太郎");
        }
        model.addAttribute("user", currentUser);

        AttendanceDto attendanceDto = attendanceService.getStatus(TEST_USER_ID);
        model.addAttribute("status", attendanceDto);

        List<LogDto> logList = logService.getLogListForMain(TEST_USER_ID);
        model.addAttribute("logList", logList);

        return "userMain";
    }
}

