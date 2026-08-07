package com.example.mypage.controller;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.mypage.dto.MyPageDto;
import com.example.mypage.form.MyPageProfileForm;
import com.example.mypage.service.MyPageService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    /* =========================================
       マイページトップ
    ========================================= */
    @GetMapping({"/user/mypage", "/admin/mypage"})
    public String index(
            @AuthenticationPrincipal UserDetails loginUser,
            HttpServletRequest request,
            Model model) {

        if (loginUser == null) return "redirect:/login";

        // ▼ user/admin の自動振り分け
        String redirectUrl = checkAndRedirect(
                loginUser, request,
                "/user/mypage", "/admin/mypage"
        );
        if (redirectUrl != null) return redirectUrl;

        model.addAttribute("currentUser", loginUser);

        boolean isAdmin = loginUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        String basePath = isAdmin ? "/admin" : "/user";
        model.addAttribute("basePath", basePath);

        String userId = loginUser.getUsername();

        // ▼ マイページ情報取得（新仕様：家具スロット対応）
        MyPageDto dto = myPageService.getMyPageData(userId);

        // ▼ 未登録なら初回登録画面へ強制遷移
        if (!dto.isProfileRegistered()) {
            model.addAttribute("profileForm", new MyPageProfileForm());
            return "mypage_initial_profile";
        }

        // ▼ 登録済なら通常のマイページ表示
        model.addAttribute("mypage", dto);
        return "mypage";
    }

    /* =========================================
       プロフィール編集画面（GET）
    ========================================= */
    @GetMapping({"/user/mypage/profile", "/admin/mypage/profile"})
    public String profileEdit(
            @AuthenticationPrincipal UserDetails loginUser,
            HttpServletRequest request,
            Model model) {

        if (loginUser == null) return "redirect:/login";

        String redirectUrl = checkAndRedirect(
                loginUser, request,
                "/user/mypage/profile", "/admin/mypage/profile"
        );
        if (redirectUrl != null) return redirectUrl;

        model.addAttribute("currentUser", loginUser);

        boolean isAdmin = loginUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        String basePath = isAdmin ? "/admin" : "/user";
        model.addAttribute("basePath", basePath);

        String userId = loginUser.getUsername();

        // ▼ 現在のプロフィール情報
        MyPageDto dto = myPageService.getMyPageData(userId);

        // ▼ 未登録なら初期登録画面へ
        if (!dto.isProfileRegistered()) {
            model.addAttribute("profileForm", new MyPageProfileForm());
            return "mypage_initial_profile";
        }

        // ▼ 登録済なら編集画面へ
        MyPageProfileForm form = new MyPageProfileForm();
        form.setNickname(dto.getNickname());
        form.setThemeColor(dto.getThemeColor());
        model.addAttribute("profileForm", form);

        return "mypage_profile";
    }

    /* =========================================
       プロフィール登録・更新（POST）
       → 初回登録なら registerInitialProfile()
       → 登録済なら updateProfile()
    ========================================= */
    @PostMapping({"/user/mypage/profile", "/admin/mypage/profile"})
    public String updateProfile(
            @AuthenticationPrincipal UserDetails loginUser,
            HttpServletRequest request,
            @ModelAttribute("profileForm") MyPageProfileForm form) {

        if (loginUser == null) return "redirect:/login";

        String redirectUrl = checkAndRedirect(
                loginUser, request,
                "/user/mypage/profile", "/admin/mypage/profile"
        );
        if (redirectUrl != null) return redirectUrl;

        String userId = loginUser.getUsername();

        // ▼ 現在の状態を取得（新仕様：家具スロット対応）
        MyPageDto dto = myPageService.getMyPageData(userId);

        // ▼ 初期セットアップが必要か判定（新仕様：家具スロット数で判定）
        boolean needInitialSetup =
                !dto.isProfileRegistered() ||
                !myPageService.hasAvatarEquipment(userId) ||
                !myPageService.hasRoomState(userId) ||
                myPageService.getPlacedFurnitureCount(userId) == 0;

        if (needInitialSetup) {
            myPageService.registerInitialProfile(userId, form);
        } else {
            myPageService.updateProfile(userId, form);
        }

        boolean isAdmin = loginUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        String basePath = isAdmin ? "/admin" : "/user";

        return "redirect:" + basePath + "/mypage";
    }

    /* =========================================
       user/admin 自動振り分け
    ========================================= */
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
