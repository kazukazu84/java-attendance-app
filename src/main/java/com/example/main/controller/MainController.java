package com.example.main.controller;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

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
    private UserInfoRepository userRepository;

    /**
     * メイン画面の表示（一般ユーザー用・管理者用の両方のURLを受け付けます）
     */
    @GetMapping({"/user/main", "/admin/main"})
    public String mainView(@AuthenticationPrincipal UserDetails loginUser, 
                           HttpServletRequest request, 
                           Model model) {
        
        if (loginUser == null) {
            // 未認証（未ログイン）状態の場合はログイン画面へリダイレクト
            return "redirect:/login";
        }

        // 1. 権限チェック＆URL正規化リダイレクト
        String redirectUrl = checkAndRedirect(loginUser, request, "/user/main", "/admin/main");
        if (redirectUrl != null) {
            return redirectUrl;
        }

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
        
        return "main";
    }
    
    /**
     * 給与確認画面（仮）
     */
    @GetMapping({"/user/salary", "/admin/salary"})
    public String salaryView(@AuthenticationPrincipal UserDetails loginUser, HttpServletRequest request) {
        if (loginUser == null) return "redirect:/login";

        String redirectUrl = checkAndRedirect(loginUser, request, "/user/salary", "/admin/salary");
        if (redirectUrl != null) return redirectUrl;

        return "tempSalary";
    }

    /**
     * シフト申請画面（仮）
     */
    @GetMapping({"/user/shift-request", "/admin/shift-request"})
    public String shiftRequestView(@AuthenticationPrincipal UserDetails loginUser, HttpServletRequest request) {
        if (loginUser == null) return "redirect:/login";

        String redirectUrl = checkAndRedirect(loginUser, request, "/user/shift-request", "/admin/shift-request");
        if (redirectUrl != null) return redirectUrl;

        return "tempShiftRequest";
    }
//
//    /**
//     * ユーザー管理画面（仮）
//     */
//    @GetMapping({"/user/user-management", "/admin/user-management"})
//    public String userManagementView(@AuthenticationPrincipal UserDetails loginUser, HttpServletRequest request) {
//        if (loginUser == null) return "redirect:/login";
//
//        String redirectUrl = checkAndRedirect(loginUser, request, "/user/user-management", "/admin/user-management");
//        if (redirectUrl != null) return redirectUrl;
//
//        return "tempUserManagement";
//    }

    /**
     * シフト管理画面（仮）
     */
    @GetMapping({"/user/shift-management", "/admin/shift-management"})
    public String shiftManagementView(@AuthenticationPrincipal UserDetails loginUser, HttpServletRequest request) {
        if (loginUser == null) return "redirect:/login";

        String redirectUrl = checkAndRedirect(loginUser, request, "/user/shift-management", "/admin/shift-management");
        if (redirectUrl != null) return redirectUrl;

        return "tempShiftManagement";
    }

    /**
     * 【共通処理】権限とリクエストURLに応じたリダイレクト判定
     */
    private String checkAndRedirect(UserDetails loginUser, HttpServletRequest request, String userPath, String adminPath) {
        boolean isAdmin = loginUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
        String requestUri = request.getRequestURI();

        // 管理者が一般用URLにアクセスした場合 ➔ 管理者用URLへ転送
        if (isAdmin && requestUri.endsWith(userPath)) {
            return "redirect:" + adminPath;
        }
        
        // 一般ユーザーが管理者用URLにアクセスした場合 ➔ 一般用URLへ転送
        if (!isAdmin && requestUri.endsWith(adminPath)) {
            return "redirect:" + userPath;
        }

        return null; // リダイレクト不要
    }
}