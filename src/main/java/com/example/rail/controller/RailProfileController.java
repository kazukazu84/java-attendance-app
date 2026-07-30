package com.example.rail.controller;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.rail.entity.RailLineMaster;
import com.example.rail.entity.RailUserProfile;
import com.example.rail.service.RailLineMasterService;
import com.example.rail.service.RailUserProfileService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class RailProfileController {

    private final RailUserProfileService profileService;
    private final RailLineMasterService lineMasterService;

    @GetMapping({"/user/rail/profile", "/admin/rail/profile"})
    public String profile(
            @AuthenticationPrincipal UserDetails loginUser,
            HttpServletRequest request,
            Model model) {

        if (loginUser == null) return "redirect:/login";

        String redirectUrl = checkAndRedirect(
                loginUser, request,
                "/user/rail/profile", "/admin/rail/profile"
        );
        if (redirectUrl != null) return redirectUrl;

        boolean isAdmin = loginUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        String basePath = isAdmin ? "/admin" : "/user";
        model.addAttribute("basePath", basePath);

        String userId = loginUser.getUsername();

        // ★ プロフィール取得
        RailUserProfile profile = profileService.findByUserId(userId).orElse(null);
        model.addAttribute("profile", profile);

        // ★ お気に入り路線のマスタ取得
        RailLineMaster lineMaster = null;
        if (profile != null && profile.getFavoriteRailCode() != null) {
            lineMaster = lineMasterService.findByRailCode(profile.getFavoriteRailCode()).orElse(null);
        }
        model.addAttribute("lineMaster", lineMaster);

        return "profile";
    }

    private String checkAndRedirect(
            UserDetails loginUser,
            HttpServletRequest request,
            String userPath,
            String adminPath) {

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
