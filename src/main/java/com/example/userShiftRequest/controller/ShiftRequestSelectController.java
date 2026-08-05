package com.example.userShiftRequest.controller;

import java.security.Principal;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.userShiftRequest.form.ShiftRequestSelectForm;
import com.example.userShiftRequest.service.ShiftRequestSelectService;

import jakarta.servlet.http.HttpSession;

/**
 * シフト申請選択画面コントローラー
 */
@Controller
public class ShiftRequestSelectController {

    @Autowired
    private ShiftRequestSelectService shiftRequestSelectService;

    @GetMapping("/user/shiftRequestSelect")
    public String showShiftRequestSelect(
            @RequestParam(required = false) Integer selectedYear,
            Principal principal,
            HttpSession session,
            Model model) {

        System.out.println("★★ shiftRequestSelect 起動 ★★");

        // 初期表示時（年度指定がない場合）は今年度をセット
        if (selectedYear == null) {
            selectedYear = LocalDate.now().getYear();
        }

        // ログインユーザーIDの取得処理（Spring Security優先、無ければSessionから取得）
        String currentUserId = null;
        if (principal != null) {
            currentUserId = principal.getName();
        } else if (session.getAttribute("userId") != null) {
            currentUserId = (String) session.getAttribute("userId");
        }

        // 動作確認用ログ
        System.out.println("選択年度 = " + selectedYear);
        System.out.println("ログインユーザーID = " + currentUserId);

        // サービス呼び出し（年度とユーザーIDを渡す）
        ShiftRequestSelectForm form = shiftRequestSelectService.getShiftList(selectedYear, currentUserId);

        // 画面（Thymeleaf）に渡すオブジェクトをセット
        model.addAttribute("form", form);

        return "shiftRequestSelect";
    }
}