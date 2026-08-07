package com.example.mypage.controller;

import java.util.ArrayList;
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

import com.example.mypage.entity.UserAvatarEquipmentEntity;
import com.example.mypage.form.AvatarForm;
import com.example.mypage.service.AvatarService;
import com.example.mypage.service.MyPageService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping({"/user/mypage/avatar", "/admin/mypage/avatar"})
public class AvatarController {

    private final AvatarService avatarService;
    private final MyPageService myPageService;

    /* ============================
       GET: アバター編集画面（最新仕様）
    ============================ */
    @GetMapping("/edit")
    public String editForm(
            @AuthenticationPrincipal UserDetails loginUser,
            HttpServletRequest request,
            Model model) {

        if (loginUser == null) return "redirect:/login";

        String redirectUrl = checkAndRedirect(loginUser, request,
                "/user/mypage/avatar/edit",
                "/admin/mypage/avatar/edit");
        if (redirectUrl != null) return redirectUrl;

        String userId = loginUser.getUsername();
        boolean isAdmin = isAdmin(loginUser);
        String basePath = isAdmin ? "/admin" : "/user";

        model.addAttribute("currentUser", loginUser);
        model.addAttribute("basePath", basePath);

        /* ▼ マイページ情報（テーマカラー含む） */
        model.addAttribute("mypage", myPageService.getMyPageData(userId));

        /* ▼ 最新仕様：部位ごとにグループ化されたアイテム */
        model.addAttribute("groupedItems", avatarService.getGroupedAvatarItems(userId));

        /* ▼ 装備中アイテム */
        UserAvatarEquipmentEntity eq = avatarService.getCurrentEquipment(userId);

        /* ▼ アバター表示用 CSS クラス一覧 */
        model.addAttribute("avatarClasses", buildAvatarCssList(eq));

        /* ▼ フォーム初期値 */
        model.addAttribute("avatarForm", buildAvatarForm(eq));

        return "avatar";
    }

    /* ============================
       POST: アバター更新
    ============================ */
    @PostMapping("/update")
    public String updateAvatar(
            @AuthenticationPrincipal UserDetails loginUser,
            HttpServletRequest request,
            @ModelAttribute AvatarForm form) {

        if (loginUser == null) return "redirect:/login";

        String redirectUrl = checkAndRedirect(loginUser, request,
                "/user/mypage/avatar/update",
                "/admin/mypage/avatar/update");
        if (redirectUrl != null) return redirectUrl;

        String userId = loginUser.getUsername();
        avatarService.updateEquipment(userId, form);

        return "redirect:" + (isAdmin(loginUser) ? "/admin/mypage" : "/user/mypage");
    }

    /* ============================
       補助メソッド
    ============================ */

    /** 装備中アイテム → CSS クラス一覧へ変換 */
    private List<String> buildAvatarCssList(UserAvatarEquipmentEntity eq) {
        List<String> list = new ArrayList<>();
        addAvatarCss(list, eq.getBaseItemId());
        addAvatarCss(list, eq.getEarItemId());
        addAvatarCss(list, eq.getEyeItemId());
        addAvatarCss(list, eq.getFaceItemId());
        addAvatarCss(list, eq.getBodyItemId());
        addAvatarCss(list, eq.getAccessoryItemId());
        return list;
    }

    /** itemId → cssClass を追加（最新仕様 AvatarService と整合） */
    private void addAvatarCss(List<String> list, Long itemId) {
        if (itemId != null) {
            avatarService.getAvatarItemById(itemId)
                    .ifPresent(item -> list.add(item.getCssClass()));
        }
    }

    /** 装備中アイテム → フォーム初期値 */
    private AvatarForm buildAvatarForm(UserAvatarEquipmentEntity eq) {
        AvatarForm form = new AvatarForm();
        form.setBaseItemId(eq.getBaseItemId());
        form.setEarItemId(eq.getEarItemId());
        form.setEyeItemId(eq.getEyeItemId());
        form.setFaceItemId(eq.getFaceItemId());
        form.setBodyItemId(eq.getBodyItemId());
        form.setAccessoryItemId(eq.getAccessoryItemId());
        return form;
    }

    /** Admin 判定 */
    private boolean isAdmin(UserDetails loginUser) {
        return loginUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    /** User/Admin のパス整合性チェック */
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
