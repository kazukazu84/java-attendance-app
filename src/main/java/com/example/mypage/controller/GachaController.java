package com.example.mypage.controller;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.mypage.dto.GachaResultDto;
import com.example.mypage.dto.MyPageDto;
import com.example.mypage.form.GachaForm;
import com.example.mypage.service.GachaService;
import com.example.mypage.service.MyPageService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping({"/user/mypage/gacha", "/admin/mypage/gacha"})
public class GachaController {

    private final GachaService gachaService;
    private final MyPageService myPageService;

    @GetMapping
    public String showGachaPage(
            @AuthenticationPrincipal UserDetails loginUser,
            HttpServletRequest request,
            Model model) {

        if (loginUser == null) return "redirect:/login";

        String redirectUrl = checkAndRedirect(
                loginUser, request,
                "/user/mypage/gacha",
                "/admin/mypage/gacha"
        );
        if (redirectUrl != null) return redirectUrl;

        model.addAttribute("currentUser", loginUser);

        boolean isAdmin = isAdmin(loginUser);
        String basePath = isAdmin ? "/admin" : "/user";
        model.addAttribute("basePath", basePath);

        String userId = loginUser.getUsername();

        MyPageDto myPage = myPageService.getMyPageData(userId);

        model.addAttribute("points", myPage.getCurrentPoints());
        model.addAttribute("recentResults", gachaService.getRecentResults(userId));
        model.addAttribute("gachaForm", new GachaForm());
        model.addAttribute("mypage", myPage);

        return "gacha";
    }

    @PostMapping("/roll")
    public String rollGacha(
            @AuthenticationPrincipal UserDetails loginUser,
            HttpServletRequest request,
            @ModelAttribute GachaForm form,
            RedirectAttributes redirectAttributes) {

        if (loginUser == null) return "redirect:/login";

        String redirectUrl = checkAndRedirect(
                loginUser, request,
                "/user/mypage/gacha/roll",
                "/admin/mypage/gacha/roll"
        );
        if (redirectUrl != null) return redirectUrl;

        String userId = loginUser.getUsername();

        try {
            if (form.getCount() == 10) {
                // 10連ガチャ
                List<GachaResultDto> results = gachaService.rollGachaMulti(userId, form.getGachaType());
                redirectAttributes.addFlashAttribute("gachaResults", results);
            } else {
                // 単発ガチャ
                GachaResultDto result = gachaService.rollGachaSingle(userId, form.getGachaType());
                redirectAttributes.addFlashAttribute("gachaResult", result);
            }

            return "redirect:" + (isAdmin(loginUser)
                    ? "/admin/mypage/gacha/result"
                    : "/user/mypage/gacha/result");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:" + (isAdmin(loginUser)
                    ? "/admin/mypage/gacha"
                    : "/user/mypage/gacha");
        }
    }

    @GetMapping("/result")
    public String showResult(
            @AuthenticationPrincipal UserDetails loginUser,
            HttpServletRequest request,
            Model model) {

        if (loginUser == null) return "redirect:/login";

        String redirectUrl = checkAndRedirect(
                loginUser, request,
                "/user/mypage/gacha/result",
                "/admin/mypage/gacha/result"
        );
        if (redirectUrl != null) return redirectUrl;

        boolean isAdmin = isAdmin(loginUser);
        model.addAttribute("basePath", isAdmin ? "/admin" : "/user");

        // ★★★ ここが今回の修正ポイント ★★★
        String userId = loginUser.getUsername();
        MyPageDto myPage = myPageService.getMyPageData(userId);
        model.addAttribute("mypage", myPage);

        return "gachaResult";
    }

    private boolean isAdmin(UserDetails loginUser) {
        return loginUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private String checkAndRedirect(
            UserDetails loginUser,
            HttpServletRequest request,
            String userPath,
            String adminPath) {

        boolean isAdmin = isAdmin(loginUser);
        String uri = request.getRequestURI();

        if (isAdmin && uri.endsWith(userPath)) return "redirect:" + adminPath;
        if (!isAdmin && uri.endsWith(adminPath)) return "redirect:" + userPath;

        return null;
    }
}
