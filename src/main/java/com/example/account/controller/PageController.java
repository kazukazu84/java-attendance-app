package com.example.account.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    @GetMapping("/user/s-user")
    public String suserPage() { return "kumeda/s-user"; }
    
    @GetMapping("/user/m-user")
    public String muserPage() { return "kumeda/m-user"; }

    @GetMapping("/admin/m-admin")
    public String madminPage() { return "kumeda/admin/m-admin"; }

    @GetMapping("/error-denied")
    public String deniedPage() { return "kumeda/error-denied"; }
}