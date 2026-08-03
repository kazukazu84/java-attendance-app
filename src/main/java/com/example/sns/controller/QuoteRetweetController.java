package com.example.sns.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.account.service.CustomUserDetails;
import com.example.sns.dto.QuoteRetweetDto;
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
            @RequestParam(required = false) Long parentQuoteId,
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

        // ▼ 元投稿
        PostEntity targetPost = postService.getPostById(postId);
        model.addAttribute("targetPost", targetPost);

        // ▼ フォーム
        QuoteRetweetForm form = new QuoteRetweetForm();
        form.setPostId(postId);
        form.setParentQuoteId(parentQuoteId);
        model.addAttribute("quoteRetweetForm", form);

        // ▼ 親引用RT
        if (parentQuoteId != null) {
            QuoteRetweetDto parentQuote = quoteRetweetService.getQuoteById(parentQuoteId);
            model.addAttribute("parentQuote", parentQuote);

            // ▼ ★ 親引用RTへの返信一覧（childrenMap）を渡す
            List<QuoteRetweetDto> children = quoteRetweetService.getChildQuotes(parentQuoteId);
            Map<Long, List<QuoteRetweetDto>> childrenMap = new HashMap<>();
            childrenMap.put(parentQuoteId, children);
            model.addAttribute("childrenMap", childrenMap);
        }

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
                form.getComment(),
                form.getParentQuoteId()
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
