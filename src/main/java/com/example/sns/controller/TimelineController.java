package com.example.sns.controller;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.account.service.CustomUserDetails;
import com.example.sns.dto.PostDto;
import com.example.sns.form.PostForm;
import com.example.sns.service.PostService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class TimelineController {

    private final PostService postService;

    @GetMapping({"/user/timeline", "/admin/timeline"})
    public String timeline(
            @AuthenticationPrincipal CustomUserDetails loginUser,
            HttpServletRequest request,
            Model model) {

        if (loginUser == null) return "redirect:/login";

        // 権限に応じてURLを正規化
        String redirectUrl = checkAndRedirect(
                loginUser, request,
                "/user/timeline", "/admin/timeline"
        );
        if (redirectUrl != null) return redirectUrl;

        // basePath を画面へ渡す
        String basePath = loginUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))
                ? "/admin"
                : "/user";
        model.addAttribute("basePath", basePath);

        String currentUserId = loginUser.getUsername();
        List<PostDto> posts = postService.getTimeline(currentUserId);

        model.addAttribute("posts", posts);
        model.addAttribute("postForm", new PostForm());
        model.addAttribute("currentUser", loginUser);

        return "timeline";
    }

    // ★ 給与管理システムと同じ権限判定ロジック
    private String checkAndRedirect(
            CustomUserDetails loginUser,
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
