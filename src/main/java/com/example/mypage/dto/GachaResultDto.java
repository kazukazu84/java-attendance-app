package com.example.mypage.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GachaResultDto {
    private Long resultId;           // ガチャ結果ID
    private Long itemId;             // アイテムID
    private String itemType;         // AVATAR / ROOM
    private String itemName;         // アイテム名
    private String rarity;           // R / SR / SSR
    private String cssClass;         // アイテム表示用CSS
    private LocalDateTime obtainedAt;// 取得日時
    private String animationClass;   // レアリティ演出用CSSクラス
}
