package com.example.sns.controller;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.account.service.CustomUserDetails;
import com.example.sns.entity.PostEntity;
import com.example.sns.form.QuoteRetweetForm;
import com.example.sns.service.PostService;
import com.example.sns.service.QuoteRetweetService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class QuoteRetweetController {

    private final QuoteRetweetService quoteRetweetService;
    private final PostService postService;

    @GetMapping({"/user/quote-retweet", "/admin/quote-retweet"})
    public String showQuoteForm(
            @AuthenticationPrincipal CustomUserDetails loginUser,
            @RequestParam Long postId,
            HttpServletRequest request,
            Model model) {

        if (loginUser == null) return "redirect:/login";

        String redirectUrl = checkAndRedirect(
                loginUser, request,
                "/user/quote-retweet", "/admin/quote-retweet"
        );
        if (redirectUrl != null) return redirectUrl;

        boolean isAdmin = loginUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        String basePath = isAdmin ? "/admin" : "/user";
        model.addAttribute("basePath", basePath);

        PostEntity targetPost = postService.getPostById(postId);

        QuoteRetweetForm form = new QuoteRetweetForm();
        form.setPostId(postId);

        model.addAttribute("targetPost", targetPost);
        model.addAttribute("quoteRetweetForm", form);

        return "quoteRetweet";
    }

    @PostMapping({"/user/quote-retweet", "/admin/quote-retweet"})
    public String createQuoteRetweet(
            @AuthenticationPrincipal CustomUserDetails loginUser,
            HttpServletRequest request,
            @ModelAttribute QuoteRetweetForm form) {

        if (loginUser == null) return "redirect:/login";

        String redirectUrl = checkAndRedirect(
                loginUser, request,
                "/user/quote-retweet", "/admin/quote-retweet"
        );
        if (redirectUrl != null) return redirectUrl;

        quoteRetweetService.createQuoteRetweet(
                form.getPostId(),
                loginUser.getUsername(),
                form.getComment()
        );

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
