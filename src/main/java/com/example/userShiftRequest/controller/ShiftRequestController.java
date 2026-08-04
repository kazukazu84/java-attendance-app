package com.example.userShiftRequest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.userShiftRequest.dto.ShiftRequestDto;
import com.example.userShiftRequest.form.ShiftRequestForm;
import com.example.userShiftRequest.service.ShiftRequestService;

@Controller
public class ShiftRequestController {

    @Autowired
    private ShiftRequestService shiftRequestService;

    /**
     * シフト申請画面表示
     */
    @GetMapping("/shiftRequest")
    public String showShiftRequest(
            @RequestParam Integer eventId,
            @RequestParam(required = false) Integer selectedYear,
            @AuthenticationPrincipal UserDetails loginUser,
            Model model) {

        System.out.println("選択イベントID = " + eventId);

        // ログインユーザーID取得
        String currentUserId =
                loginUser.getUsername();

        // Shiftsテーブルからシフト情報取得
        ShiftRequestForm form =
                shiftRequestService.getShiftRequestInfo(
                        eventId,
                        currentUserId);

        form.setEventId(eventId);
        form.setSelectedYear(selectedYear);

        model.addAttribute("form", form);

        return "/shiftRequest";
    }

    /**
     * シフト申請
     */
    @PostMapping("/shiftRequest/apply")
    public String applyShiftRequest(
            ShiftRequestForm form,
            RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal UserDetails loginUser) {

        String currentUserId =
                loginUser.getUsername();

        System.out.println("currentUserId = " + currentUserId);

        if (form.getShiftList() != null) {

            for (ShiftRequestDto dto : form.getShiftList()) {

                System.out.println("日付 = " + dto.getWorkDate());
                System.out.println("可否 = " + dto.getAvailable());
                System.out.println("出勤 = " + dto.getStartTime());
                System.out.println("退勤 = " + dto.getEndTime());

            }
        }

        boolean result =
                shiftRequestService.applyShiftRequest(
                        form,
                        currentUserId);

        if (result) {

            redirectAttributes.addFlashAttribute(
                    "message",
                    "申請が完了しました。");

        } else {

            redirectAttributes.addFlashAttribute(
                    "message",
                    "入力内容を確認してください。");
        }

        return "redirect:/shiftRequest?eventId="
                + form.getEventId();
    }

}