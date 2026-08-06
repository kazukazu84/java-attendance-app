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


        String currentUserId = getCurrentUserId(principal, session);


        ShiftRequestForm form =
                shiftRequestService.getShiftRequestInfo(
                        eventId,
                        currentUserId);


        form.setEventId(eventId);


        model.addAttribute("form", form);


        return "shiftRequest";
    }



    /**
     * シフト申請登録処理
     */
    @PostMapping("/user/shiftRequest")
    public String applyShiftRequest(
            @ModelAttribute("form") ShiftRequestForm form,
            Principal principal,
            HttpSession session,
            Model model) {


        String currentUserId =
                getCurrentUserId(principal, session);



        boolean success =
                shiftRequestService.applyShiftRequest(
                        form,
                        currentUserId);



        if (success) {

            model.addAttribute(
                    "successMessage",
                    "申請しました");

        } else {

            model.addAttribute(
                    "errorMessage",
                    "申請処理に失敗しました。入力内容を確認してください。");
        }



        model.addAttribute(
                "form",
                form);



        return "shiftRequest";
    }



    /**
     * ログインユーザー取得
     *
     * Spring Security利用時：
     * principal.getName()
     *
     * セッションログイン利用時：
     * session userId
     *
     */
    private String getCurrentUserId(
            Principal principal,
            HttpSession session) {


        if (principal != null) {

            return principal.getName();

        }


        Object userId =
                session.getAttribute("userId");


        if (userId != null) {

            return userId.toString();

        }


        return null;
    }

}