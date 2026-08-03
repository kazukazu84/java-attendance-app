package com.example.sns.controller;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.account.service.CustomUserDetails;
import com.example.sns.service.RetweetService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class RetweetController {

    private final RetweetService retweetService;

    @PostMapping({"/user/retweet", "/admin/retweet"})
    public String toggleRetweet(
            @RequestParam Long postId,
            @AuthenticationPrincipal CustomUserDetails loginUser,
            HttpServletRequest request) {

        if (loginUser == null) return "redirect:/login";

        String redirectUrl = checkAndRedirect(
                loginUser, request,
                "/user/retweet", "/admin/retweet"
        );
        if (redirectUrl != null) return redirectUrl;

        retweetService.toggleRetweet(postId, loginUser.getUsername());

        boolean isAdmin = loginUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        return "redirect:" + (isAdmin ? "/admin/timeline" : "/user/timeline");
    }

    private String checkAndRedirect(CustomUserDetails loginUser, HttpServletRequest request,
                                    String userPath, String adminPath) {

        boolean isAdmin = loginUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        String requestUri = request.getRequestURI();

        if (isAdmin && requestUri.endsWith(userPath)) {
            return "redirect:" + adminPath;
        }

        if (!isAdmin && requestUri.endsWith(adminPath)) {
            return "redirect:" + userPath;
        }

        return null;
    }
}
