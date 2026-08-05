package com.example.userShiftRequest.controller;

import java.security.Principal;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.userShiftRequest.form.ShiftRequestForm;
import com.example.userShiftRequest.service.ShiftRequestService;

/**
 * シフト申請入力・表示コントローラー
 */
@Controller
public class ShiftRequestController {

    @Autowired
    private ShiftRequestService shiftRequestService;

    /**
     * シフト申請入力画面表示
     */
    @GetMapping("/user/shiftRequest")
    public String showShiftRequest(
            @RequestParam("eventId") Integer eventId,
            Principal principal,
            HttpSession session,
            Model model) {

        System.out.println("★★ shiftRequest 起動 (eventId=" + eventId + ") ★★");

        // ログインユーザーID取得
        String currentUserId = null;
        if (principal != null) {
            currentUserId = principal.getName();
        } else if (session.getAttribute("userId") != null) {
            currentUserId = (String) session.getAttribute("userId");
        }

        // 【修正】型名を ShiftRequestForm に修正
        ShiftRequestForm form = shiftRequestService.getShiftRequestInfo(eventId, currentUserId);

        // フォームオブジェクトに eventId を確実に入れておく
        form.setEventId(eventId);

        // 【統一】HTML側（${form.xxx}）に合わせて "form" でセット
        model.addAttribute("form", form);

        return "shiftRequest";
    }

    /**
     * シフト申請登録処理
     */
    @PostMapping("/user/shiftRequest")
    public String applyShiftRequest(
            // 【統一】ModelAttributeの名称も "form" に統一
            @ModelAttribute("form") ShiftRequestForm form,
            Principal principal,
            HttpSession session,
            Model model) {

        // ログインユーザーID取得
        String currentUserId = null;
        if (principal != null) {
            currentUserId = principal.getName();
        } else if (session.getAttribute("userId") != null) {
            currentUserId = (String) session.getAttribute("userId");
        }

        System.out.println("★★ shiftRequest 申請実行 (eventId=" + form.getEventId() + ", userId=" + currentUserId + ") ★★");

        // 登録・更新処理の実行
        boolean success = shiftRequestService.applyShiftRequest(form, currentUserId);

        if (success) {
            // リダイレクト先の先頭に "/" を付与
            return "redirect:/user/shiftRequestSelect";
        } else {
            model.addAttribute("errorMessage", "申請処理に失敗しました。入力内容を確認してください。");
            // 【修正】ここも "form" に統一
            model.addAttribute("form", form);
            return "shiftRequest";
        }
    }
}