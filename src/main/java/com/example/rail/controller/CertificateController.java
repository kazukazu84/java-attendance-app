package com.example.rail.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.rail.dto.CertificateDto;
import com.example.rail.dto.DelayInfoDto;
import com.example.rail.form.CertificateForm;
import com.example.rail.service.DelayCertificateService;
import com.example.rail.service.RailOperationService;
import com.example.rail.validation.CertificateRequestValidator;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/rail")
@RequiredArgsConstructor
public class CertificateController {

    private final RailOperationService railOperationService;
    private final DelayCertificateService delayCertificateService;
    private final CertificateRequestValidator certificateRequestValidator;

    @GetMapping("/certificate/form")
    public String showForm(Model model) {
        model.addAttribute("certificateForm", new CertificateForm());
        return "certificateForm";
    }

    @PostMapping("/certificate")
    public String issueCertificate(@ModelAttribute CertificateForm form, Model model) {
        certificateRequestValidator.validate(form);

        DelayInfoDto delayInfo = railOperationService.getDelayInfo(form.getLineName());
        CertificateDto certificateDto = delayCertificateService.generateCertificate(delayInfo, form.getUserId());

        model.addAttribute("certificate", certificateDto);
        return "certificateResult";
    }
}