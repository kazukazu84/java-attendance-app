package com.example.adminshift.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.adminshift.dto.ShiftApplicationEventSelectDto;
import com.example.adminshift.dto.ShiftRequestDetailDto;
import com.example.adminshift.dto.ShiftRequestUserSummaryDto;
import com.example.adminshift.service.AdminShiftRequestService;

@Controller
@RequestMapping("/admin/shift-request-list")
public class AdminShiftRequestController {

    private final AdminShiftRequestService adminShiftRequestService;

    public AdminShiftRequestController(AdminShiftRequestService adminShiftRequestService) {
        this.adminShiftRequestService = adminShiftRequestService;
    }

    @GetMapping
    public String index(@RequestParam(name = "eventId", required = false) Integer selectedEventId, Model model) {
        List<ShiftApplicationEventSelectDto> events = adminShiftRequestService.getTargetEvents();

        if (selectedEventId == null && !events.isEmpty()) {
            selectedEventId = adminShiftRequestService.determineDefaultEventId(events);
        }

        List<ShiftRequestUserSummaryDto> userSummaries = adminShiftRequestService.getUserSummaryList(selectedEventId);

        model.addAttribute("events", events);
        model.addAttribute("selectedEventId", selectedEventId);
        model.addAttribute("userSummaries", userSummaries);

        return "admin/shift-request-list";
    }

    /**
     * サイドバー用Ajax API (詳細データ取得)
     */
    @GetMapping("/detail")
    @ResponseBody
    public ResponseEntity<List<ShiftRequestDetailDto>> getDetail(
            @RequestParam("userId") String userId,
            @RequestParam("eventId") Integer eventId) {
        
        List<ShiftRequestDetailDto> details = adminShiftRequestService.getRequestDetails(userId, eventId);
        return ResponseEntity.ok(details);
    }
}