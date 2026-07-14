package com.example.account.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    @GetMapping("/user/s-user")
    public String suserPage() { return "account/user/s-user"; }
    
    @GetMapping("/user/m-user")
    public String muserPage() { return "account/user/m-user"; }

    @GetMapping("/admin/m-admin")
    public String madminPage() { return "account/admin/m-admin"; }

    @GetMapping("/error-denied")
    public String deniedPage() { return "account/error-denied"; }
}