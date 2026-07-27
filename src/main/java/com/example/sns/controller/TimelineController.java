package com.example.sns.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.account.service.CustomUserDetails;
import com.example.sns.dto.PostDto;
import com.example.sns.dto.QuoteRetweetDto;
import com.example.sns.form.PostForm;
import com.example.sns.service.PostService;
import com.example.sns.service.QuoteRetweetService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class TimelineController {

    private final PostService postService;
    private final QuoteRetweetService quoteRetweetService;

    @GetMapping({"/user/timeline", "/admin/timeline"})
    public String timeline(
            @AuthenticationPrincipal CustomUserDetails loginUser,
            HttpServletRequest request,
            Model model) {

        if (loginUser == null) return "redirect:/login";

        String redirectUrl = checkAndRedirect(
                loginUser, request,
                "/user/timeline", "/admin/timeline"
        );
        if (redirectUrl != null) return redirectUrl;

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

        // ▼ 最新3件プレビュー
        Map<Long, List<QuoteRetweetDto>> quotePreviewMap = new HashMap<>();

        // ▼ 全引用RT一覧（元投稿への引用RTのみ）
        Map<Long, List<QuoteRetweetDto>> quoteListMap = new HashMap<>();

        // ▼ 数珠構造（親引用ID → 子引用一覧）
        Map<Long, List<QuoteRetweetDto>> childrenMap = new HashMap<>();

        for (PostDto post : posts) {

            // ▼ 元投稿への引用RT（parentQuoteId = null）
            List<QuoteRetweetDto> rootQuotes = quoteRetweetService.getRootQuotes(post.getPostId());
            quoteListMap.put(post.getPostId(), rootQuotes);

            // ▼ 最新3件プレビュー
            quotePreviewMap.put(post.getPostId(),
                    rootQuotes.stream().limit(3).toList()
            );

            // ▼ 子引用RT（parentQuoteId = quoteId）
            for (QuoteRetweetDto root : rootQuotes) {
                List<QuoteRetweetDto> children =
                        quoteRetweetService.getChildQuotes(root.getQuoteId());

                if (!children.isEmpty()) {
                    childrenMap.put(root.getQuoteId(), children);
                }
            }
        }

        // ▼ 画面へ渡す
        model.addAttribute("quotePreviewMap", quotePreviewMap);
        model.addAttribute("quoteListMap", quoteListMap);
        model.addAttribute("childrenMap", childrenMap);

        return "timeline";
    }

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
