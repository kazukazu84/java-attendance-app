package com.example.main.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.attendance.dto.AttendanceDto;
import com.example.attendance.entity.TempUserInfo;
import com.example.attendance.repository.TempUserInfoRepository;
import com.example.attendance.service.AttendanceService;
import com.example.main.dto.LogDto;
import com.example.main.service.LogService;
import com.example.main.service.UserShiftService;

@Controller
public class UserMainController {
    @Autowired
    private LogService logService;

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private UserShiftService userShiftService;
    
    @Autowired
    private TempUserInfoRepository userRepository;

    private final String TEST_USER_ID = "1";

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

        /*
         * シフト表示処理
         * ShiftScheduleRepository完成後に実装予定
         *
         * 月間表示
         * userShiftService.getMonthlyShift()
         *
         * 週間表示
         * userShiftService.getWeeklyShift()
         */
        
        return "userMain";
        
    }
    
    // 👈 【追加】給与確認画面（仮）
    @GetMapping("/user/salary")
    public String salaryView() {
        return "tempSalary";
    }

    // 👈 【追加】シフト申請画面（仮）
    @GetMapping("/user/shift-request")
    public String shiftRequestView() {
        return "tempShiftRequest";
    }

}
