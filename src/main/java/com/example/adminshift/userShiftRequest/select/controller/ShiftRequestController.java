package com.example.adminshift.userShiftRequest.select.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.adminshift.userShiftRequest.select.form.ShiftRequestForm;

@Controller
public class ShiftRequestController {

    @GetMapping("/shiftRequest")
    public String showShiftRequest(Model model) {

        ShiftRequestForm form = new ShiftRequestForm();

        form.setTargetPeriod("12/22～12/29");

        model.addAttribute("form", form);

        return "shiftRequest";
    }
}