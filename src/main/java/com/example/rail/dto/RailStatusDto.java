package com.example.rail.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RailStatusDto {

    private String company;       // 例: Yahoo!路線情報
    private String lineName;      // 例: 山手線

    // ★ 最新仕様：Yahoo!側の分類をそのまま返す
    private String statusText;    // 例: 平常運転 / 遅延 / 運休 / 見合わせ / その他 / 運転計画

    private String detailText;    // 例: 大崎駅で発生した旅客転落の影響で〜（そのまま）
    private String updatedText;   // 例: 7月27日 15時20分更新（そのまま）

    private LocalDateTime lastUpdated; // システム側の取得時刻
}