package com.example.rail.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.rail.entity.RailLineMaster;
import com.example.rail.entity.RailUserProfile;
import com.example.rail.form.RailUserProfileForm;
import com.example.rail.service.RailLineMasterService;
import com.example.rail.service.RailUserProfileService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class RailProfileSetupController {

    private final RailLineMasterService lineMasterService;
    private final RailUserProfileService profileService;

    @GetMapping({"/user/rail/profile/setup", "/admin/rail/profile/setup"})
    public String setup(
            @AuthenticationPrincipal UserDetails loginUser,
            HttpServletRequest request,
            Model model) {

        if (loginUser == null) return "redirect:/login";

        String redirectUrl = checkAndRedirect(
                loginUser, request,
                "/user/rail/profile/setup", "/admin/rail/profile/setup"
        );
        if (redirectUrl != null) return redirectUrl;

        boolean isAdmin = loginUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        String basePath = isAdmin ? "/admin" : "/user";
        model.addAttribute("basePath", basePath);

        var lines = lineMasterService.findAll();

        // ★ 会社ごとにグループ化
        Map<String, List<RailLineMaster>> grouped =
                lines.stream().collect(Collectors.groupingBy(RailLineMaster::getCompanyName));

        model.addAttribute("linesGroupedByCompany", grouped);
        model.addAttribute("form", new RailUserProfileForm());

        return "profileSetup";
    }


    @PostMapping({"/user/rail/profile/setup", "/admin/rail/profile/setup"})
    public String save(
            @AuthenticationPrincipal UserDetails loginUser,
            HttpServletRequest request,
            @ModelAttribute RailUserProfileForm form) {

        if (loginUser == null) return "redirect:/login";

        String redirectUrl = checkAndRedirect(
                loginUser, request,
                "/user/rail/profile/setup", "/admin/rail/profile/setup"
        );
        if (redirectUrl != null) return redirectUrl;

        String userId = loginUser.getUsername();

        // ★ 既存プロフィールを取得
        RailUserProfile profile = profileService.findByUserId(userId).orElse(null);

        if (profile == null) {
            // ★ 新規作成
            profile = RailUserProfile.builder()
                    .userId(userId)
                    .favoriteRailCode(form.getFavoriteRailCode())
                    .build();
        } else {
            // ★ 更新
            profile.setFavoriteRailCode(form.getFavoriteRailCode());
        }

        profileService.save(profile);  // ← 新規なら INSERT、既存なら UPDATE

        String basePath = loginUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))
                ? "/admin"
                : "/user";

        return "redirect:" + basePath + "/rail/status";
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
