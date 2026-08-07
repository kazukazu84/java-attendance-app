package com.example.mypage.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyPageDto {

    private String userId;
    private String nickname;
    private String themeColor;

    // ▼ アバター表示用
    private List<String> avatarCssClasses;
    private boolean avatarVisible;

    // ▼ ルームテーマ
    private String roomThemeCssClass;
    private String roomLayoutPattern;

    // ▼ 家具スロット（新仕様：1〜6）
    // slotItemIds.get(1) → 家具ID（Long）
    private List<Long> slotItemIds;

    // ▼ 家具の CSS クラス（プレビュー用）
    // slotCssClasses.get(1) → CSSクラス（String）
    private List<String> slotCssClasses;

    private Integer currentPoints;
    private boolean profileRegistered;
}
