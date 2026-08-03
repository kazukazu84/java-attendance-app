package com.example.rail.controller;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.rail.entity.RailLineMaster;
import com.example.rail.entity.RailUserProfile;
import com.example.rail.extractor.RailLineExtractor;
import com.example.rail.service.RailLineMasterService;
import com.example.rail.service.RailUserProfileService;
import com.example.rail.service.impl.RailFetcherImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class RailStatusController {

    private final RailLineMasterService lineMasterService;
    private final RailUserProfileService profileService;
    private final RailFetcherImpl railFetcher;
    private final RailLineExtractor railLineExtractor;

    @GetMapping({"/user/rail/status", "/admin/rail/status"})
    public String status(
            @AuthenticationPrincipal UserDetails loginUser,
            HttpServletRequest request,
            Model model) {

        if (loginUser == null) return "redirect:/login";

        String redirectUrl = checkAndRedirect(
                loginUser, request,
                "/user/rail/status", "/admin/rail/status"
        );
        if (redirectUrl != null) return redirectUrl;

        String basePath = loginUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))
                ? "/admin"
                : "/user";
        model.addAttribute("basePath", basePath);

        String userId = loginUser.getUsername();

        // ▼ 1. 初回スクレイピング（マスタが空の場合のみ）
        if (lineMasterService.count() == 0) {

            log.info("[RailStatusController] ★ 初回スクレイピング開始");

            String html = railFetcher.fetchAreaHtml();
            String jsonText = railLineExtractor.extractJsonFromHtml(html);

            var lines = railLineExtractor.extractFromJson(jsonText);

            lineMasterService.saveAll(lines);

            log.info("[RailStatusController] ★ 初回スクレイピング完了：保存件数 = {}", lines.size());
        }

        // ▼ 2. プロフィールが未設定なら設定画面へ
        RailUserProfile profile = profileService.findByUserId(userId).orElse(null);
        if (profile == null) {
            log.info("[RailStatusController] ★ プロフィール未設定 → 設定画面へ遷移");
            return "redirect:" + basePath + "/rail/profile/setup";
        }

        // ▼ 3. 路線マスタから URL を取得
        RailLineMaster master = lineMasterService.findByRailCode(profile.getFavoriteRailCode())
                .orElse(null);

        if (master == null) {
            log.warn("[RailStatusController] ★ favoriteRailCode={} のマスタが存在しません → プロフィール再設定へ",
                    profile.getFavoriteRailCode());
            return "redirect:" + basePath + "/rail/profile/setup";
        }

        // ▼ 4. 運行状況取得
        log.info("[RailStatusController] ★ 運行状況取得開始：{}", master.getDiainfoUrl());
        var status = railFetcher.fetchStatus(master.getDiainfoUrl());
        log.info("[RailStatusController] ★ 運行状況取得完了");

        model.addAttribute("status", status);
        model.addAttribute("profile", profile);
        model.addAttribute("lineMaster", master);

        return "railStatus";
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
