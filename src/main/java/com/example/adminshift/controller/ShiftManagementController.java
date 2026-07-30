package com.example.adminshift.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.adminshift.form.ShiftManagementForm;
import com.example.adminshift.service.ShiftManagementService;

/**
 * シフト管理画面用Controller
 * 既存処理・DBへは一切干渉せず、画面表示のみを行います。
 */
@Controller
@RequestMapping("/admin/shift-management")
public class ShiftManagementController {

    private final ShiftManagementService shiftManagementService;

    public ShiftManagementController(ShiftManagementService shiftManagementService) {
        this.shiftManagementService = shiftManagementService;
    }

    /**
     * シフト管理画面 初期表示
     */
    @GetMapping
    public String index(Model model) {
        model.addAttribute("shiftManagementForm", new ShiftManagementForm());
        
        // templates/admin/shiftManagement.html を返却するため "admin/shiftManagement" を指定
        return "admin/shiftManagement";
    }
}