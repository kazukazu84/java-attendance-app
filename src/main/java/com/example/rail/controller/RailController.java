package com.example.rail.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.rail.dto.RailStatusDto;
import com.example.rail.form.RailSearchForm;
import com.example.rail.service.RailOperationService;
import com.example.rail.service.impl.RailFetcherImpl;
import com.example.rail.validation.RailLineValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/rail")
@RequiredArgsConstructor
@Slf4j
public class RailController {

    private final RailOperationService railOperationService;
    private final RailLineValidator railLineValidator;

    @GetMapping("/status")
    public String getStatus(@ModelAttribute RailSearchForm form, Model model) {

        railLineValidator.validate(form);

        String lineName = (form.getLineName() != null && !form.getLineName().isBlank()) 
                ? form.getLineName() : "山手線";

        RailStatusDto statusDto = railOperationService.getStatus(lineName);

        model.addAttribute("railStatus", statusDto);
        model.addAttribute("searchForm", form);

        return "railStatus";
    }
    
    @Controller
    @RequiredArgsConstructor
    public class RailDebugController {

        private final RailFetcherImpl railFetcher;

        @GetMapping("/rail/debug/area6")
        @ResponseBody
        public String debugArea6() {
            return railFetcher.fetchAreaHtml();
        }
    }

}
