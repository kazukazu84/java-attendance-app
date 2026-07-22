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
    private UserInfoRepository userRepository;

    /**
     * メイン画面の表示（URLと名前が完全に一致して分かりやすくなります）
     */
    @GetMapping("/user/main")
    public String mainView(@AuthenticationPrincipal UserDetails loginUser, Model model) {
        
        if (loginUser == null) {
            // 未認証（未ログイン）状態の場合はログイン画面へリダイレクト
            return "redirect:/login";
        }

        // 💡 1. 権限チェック：管理者の場合は /admin/main へ強制リダイレクト
        boolean isAdmin = loginUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
        if (isAdmin) {
            return "redirect:/admin/main";
        }

        // 2. ログイン中のユーザーIDを取得
        String currentUserId = loginUser.getUsername();

        // 2. DBから UserInfo テーブルのレコードを取得（ここで username も取得されます）
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

        // 4. ログインユーザーのログ一覧を取得（currentUserId に変更）
        List<LogDto> logList = logService.getLogListForMain(currentUserId);
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
