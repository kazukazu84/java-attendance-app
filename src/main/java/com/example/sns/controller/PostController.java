package com.example.sns.controller;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.account.service.CustomUserDetails;
import com.example.sns.form.PostForm;
import com.example.sns.service.PostService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping({"/user/post", "/admin/post"})
    public String showPostForm(
            @AuthenticationPrincipal CustomUserDetails loginUser,
            HttpServletRequest request,
            Model model) {

        if (loginUser == null) return "redirect:/login";

        String redirectUrl = checkAndRedirect(
                loginUser, request,
                "/user/post", "/admin/post"
        );
        if (redirectUrl != null) return redirectUrl;

        String basePath = loginUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))
                ? "/admin"
                : "/user";
        model.addAttribute("basePath", basePath);

        model.addAttribute("postForm", new PostForm());
        return "post";
    }

    @PostMapping({"/user/post", "/admin/post"})
    public String createPost(
            @AuthenticationPrincipal CustomUserDetails loginUser,
            HttpServletRequest request,
            @ModelAttribute PostForm postForm) {

        if (loginUser == null) return "redirect:/login";

        String redirectUrl = checkAndRedirect(
                loginUser, request,
                "/user/post", "/admin/post"
        );
        if (redirectUrl != null) return redirectUrl;

        postService.createPost(loginUser.getUsername(), postForm.getContent());

        // ★ ここを修正（getPosition() ではなく authorities で判定）
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
