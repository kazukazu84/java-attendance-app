package com.example.mypage.controller;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.mypage.entity.UserRoomFurnitureEntity;
import com.example.mypage.entity.UserRoomStateEntity;
import com.example.mypage.form.RoomForm;
import com.example.mypage.service.MyPageService;
import com.example.mypage.service.RoomService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping({"/user/mypage/room", "/admin/mypage/room"})
public class RoomController {

    private final RoomService roomService;
    private final MyPageService myPageService;

    /* =========================================
       マイルーム編集画面
    ========================================= */
 @GetMapping("/edit")
 public String editForm(
         @AuthenticationPrincipal UserDetails loginUser,
         HttpServletRequest request,
         Model model) {

     if (loginUser == null) return "redirect:/login";

     String redirectUrl = checkAndRedirect(
             loginUser, request,
             "/user/mypage/room/edit",
             "/admin/mypage/room/edit"
     );
     if (redirectUrl != null) return redirectUrl;

     model.addAttribute("currentUser", loginUser);

     boolean isAdmin = isAdmin(loginUser);
     String basePath = isAdmin ? "/admin" : "/user";
     model.addAttribute("basePath", basePath);

     String userId = loginUser.getUsername();

     // ▼ マイページ全体データ
     model.addAttribute("mypage", myPageService.getMyPageData(userId));

     // ▼ 所持アイテム（テーマ・家具）※ slotIndex を含む最新仕様
     model.addAttribute("items", roomService.getOwnedRoomItems(userId));

     // ▼ 現在のルーム状態
     UserRoomStateEntity state = roomService.getCurrentRoomState(userId);

     // ▼ 編集フォーム（テーマ・レイアウト・アバターのみ）
     RoomForm form = new RoomForm();
     form.setThemeItemId(state.getThemeItemId());
     form.setLayoutPattern(state.getLayoutPattern());
     form.setAvatarVisible(state.getAvatarVisible());

     model.addAttribute("roomForm", form);

     return "room";
 }

    /* =========================================
       マイルーム更新処理（新仕様：家具6スロット）
    ========================================= */
    @PostMapping("/update")
    public String updateRoom(
            @AuthenticationPrincipal UserDetails loginUser,
            HttpServletRequest request,
            @ModelAttribute RoomForm form) {

        if (loginUser == null) return "redirect:/login";

        String redirectUrl = checkAndRedirect(
                loginUser, request,
                "/user/mypage/room/update",
                "/admin/mypage/room/update"
        );
        if (redirectUrl != null) return redirectUrl;

        String userId = loginUser.getUsername();

        // ▼ 現在の家具配置を取得（既存家具を維持するため）
        var placedFurniture = roomService.getPlacedFurniture(userId);

        // ▼ スロットごとの現在家具をマップ化
        Map<Integer, Long> oldFurniture = new HashMap<>();
        for (UserRoomFurnitureEntity f : placedFurniture) {
            oldFurniture.put(f.getSlotIndex(), f.getFurnitureItemId());
        }

        // ▼ 新仕様：null → 変更なし、0 → 未配置
        for (int slot = 1; slot <= 6; slot++) {

            Long newItemId = form.getSlotItemId(slot);   // null / 0 / itemId
            Long oldItemId = oldFurniture.get(slot);     // 現在の家具

            if (newItemId == null) {
                // ★ 何も選択されていない → 既存家具を維持
                form.setSlotItemId(slot, oldItemId);
            } else if (newItemId == 0) {
                // ★ 未配置 → 家具を外す
                form.setSlotItemId(slot, null);
            }
            // itemId の場合はそのまま form に残す
        }

        // ▼ 更新処理（form は整理済み）
        roomService.updateRoom(userId, form);

        return "redirect:" + (isAdmin(loginUser) ? "/admin/mypage" : "/user/mypage");
    }

    /* =========================================
       管理者判定
    ========================================= */
    private boolean isAdmin(UserDetails loginUser) {
        return loginUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    /* =========================================
       user/admin の URL 自動補正
    ========================================= */
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
