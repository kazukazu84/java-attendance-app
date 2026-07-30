package com.example.main.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.account.entity.UserInfo;
import com.example.account.repository.UserInfoRepository;
import com.example.attendance.dto.AttendanceDto;
import com.example.attendance.service.AttendanceService;
import com.example.main.dto.LogDto;
import com.example.main.entity.ShiftScheduleTest;
import com.example.main.service.LogService;
import com.example.main.service.ShiftScheduleTestService;
import com.example.main.service.UserShiftService;

@Controller
public class MainController {
    @Autowired
    private LogService logService;

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private UserShiftService userShiftService;
    
    @Autowired
    private ShiftScheduleTestService shiftScheduleTestService;
    
    @Autowired
    private UserInfoRepository userRepository;

    /**
     * メイン画面の表示（一般ユーザー用・管理者用の両方のURLを受け付けます）
     */
    @GetMapping({"/user/main", "/admin/main"})
    public String mainView(@AuthenticationPrincipal UserDetails loginUser,
            HttpServletRequest request,

            @RequestParam(required = false)
            Integer month,

            @RequestParam(required = false)
            Integer week,
            
            @RequestParam(required = false)
            String mode,

            Model model) {
        
        if (loginUser == null) {
            // 未認証（未ログイン）状態の場合はログイン画面へリダイレクト
            return "redirect:/login";
        }

        // 1. 権限チェック＆URL正規化リダイレクト
      //  String redirectUrl = checkAndRedirect(loginUser, request, "/user/main", "/admin/main");
        //if (redirectUrl != null) {
          //  return redirectUrl;
        //}

        // 2. ログイン中のユーザーIDを取得
        String currentUserId = loginUser.getUsername();

        // 3. DBから UserInfo テーブルのレコードを取得
        UserInfo currentUser = userRepository.findById(currentUserId).orElse(null);
        if (currentUser == null) {
            // 【デバッグ用】DBからユーザーが見つからなかった場合のログ出力
            System.out.println("[WARN] UserInfoテーブルに ID: '" + currentUserId + "' のデータが存在しません。");
            
            // 例外処理用：画面崩れ防止のためのフォールバック設定
            currentUser = new UserInfo();
            currentUser.setUserId(currentUserId);
            currentUser.setUserName("（名称未設定）"); // "ゲスト" から変更
        }
        model.addAttribute("user", currentUser);

        // 3. ログインユーザーの勤怠ステータスを取得（currentUserId に変更）
        AttendanceDto attendanceDto = attendanceService.getStatus(currentUserId);
        model.addAttribute("status", attendanceDto);

        // 4. ログ一覧を取得（引数を currentUserId から loginUser に変更）
        // 【修正前】 List<LogDto> logList = logService.getLogListForMain(currentUserId);
        List<LogDto> logList = logService.getLogListForMain(loginUser);
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
        boolean weeklyMode =  "week".equals(mode);
        
        if (week == null) {
            week = 1;
        }

        if (month == null) {
            month = 7;
        }

        LocalDate monthStart =
                LocalDate.of(2026, month, 1);

        LocalDate monthEnd =
                monthStart.withDayOfMonth(
                        monthStart.lengthOfMonth());

        int startDay;

        switch (week) {

            case 2:
                startDay = 8;
                break;

            case 3:
                startDay = 15;
                break;

            case 4:
                startDay = 22;
                break;

            case 5:
                startDay = 29;
                break;

            default:
                startDay = 1;
                break;
        }

        LocalDate startDate =
                LocalDate.of(2026, month, startDay);

        LocalDate endDate =
                startDate.plusDays(6);

        if (endDate.isAfter(monthEnd)) {

            endDate = monthEnd;

        }
    
    List<ShiftScheduleTest> monthlyShiftList =
            shiftScheduleTestService.getShiftByWeek(
                    monthStart,
                    monthEnd);

    List<ShiftScheduleTest> weeklyShiftList =
            shiftScheduleTestService.getShiftByWeek(
                    startDate,
                    endDate);
    List<Integer> days = new ArrayList<>();

    int blankDays =
            monthStart.getDayOfWeek().getValue() % 7;

    for (int i = 0; i < blankDays; i++) {

        days.add(0);

    }

    for (int day = 1;
            day <= monthStart.lengthOfMonth();
            day++) {

        days.add(day);

    }

    while (days.size() % 7 != 0) {

        days.add(0);

    }

    List<List<Integer>> calendarWeeks =
            new ArrayList<>();

    for (int i = 0; i < days.size(); i += 7) {

        List<Integer> weekList =
                new ArrayList<>();

        for (int j = i;
                j < Math.min(i + 7, days.size());
                j++) {

            weekList.add(days.get(j));

        }

        calendarWeeks.add(weekList);

    }
    model.addAttribute(
            "monthlyShiftList",
            monthlyShiftList);

    model.addAttribute(
            "weeklyShiftList",
            weeklyShiftList);

    model.addAttribute(
            "calendarWeeks",
            calendarWeeks);

    model.addAttribute(
            "month",
            month);

    model.addAttribute(
            "week",
            week);

    model.addAttribute(
            "weeklyMode",
            weeklyMode);

    return "main";

    }
}