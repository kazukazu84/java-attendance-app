package com.example.userShiftRequest.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.userShiftRequest.form.ShiftRequestSelectForm;
import com.example.userShiftRequest.service.ShiftRequestSelectService;


// シフト申請選択画面

@Controller
public class ShiftRequestSelectController {

    @Autowired
    private ShiftRequestSelectService shiftRequestSelectService;


    /**
     * シフト申請選択画面表示
     */
    @GetMapping("/user/shiftRequestSelect")
    public String showShiftRequestSelect(
            @RequestParam(required = false) Integer selectedYear,
            Model model,
            @AuthenticationPrincipal UserDetails loginUser) {


        System.out.println("★★ shiftRequestSelect 起動 ★★");


        // 年度未指定の場合は現在年度
        if (selectedYear == null) {
            selectedYear = LocalDate.now().getYear();
        }


        // ログインユーザーID取得
        String currentUserId =
                loginUser.getUsername();


        // 動作確認用ログ
        System.out.println(
                "選択年度 = " + selectedYear);

        System.out.println(
                "ログインユーザーID = " + currentUserId);


        /*
         * イベント一覧取得
         * 提出状態判定のためuserIdをServiceへ渡す
         */
        ShiftRequestSelectForm form =
                shiftRequestSelectService
                    .getShiftList(
                            selectedYear,
                            currentUserId);


        form.setSelectedYear(selectedYear);


        model.addAttribute(
                "form",
                form);


        return "shiftRequestSelect";
    }
}