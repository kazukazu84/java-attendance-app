package com.example.mypage.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.mypage.entity.RoomItemEntity;
import com.example.mypage.repository.RoomItemRepository;

@Component
public class RoomItemInitializer implements CommandLineRunner {

    @Autowired
    private RoomItemRepository roomItemRepository;

    @Override
    public void run(String... args) throws Exception {

        // ★ テーブルが空なら初期データ投入
        if (roomItemRepository.count() == 0) {

            /* ============================
               初期テーマ（N）
            ============================ */
            roomItemRepository.save(RoomItemEntity.builder()
                    .type("THEME")
                    .name("ベーシック壁紙")
                    .cssClass("theme-basic")
                    .rarity("N")
                    .build());

            roomItemRepository.save(RoomItemEntity.builder()
                    .type("THEME")
                    .name("モダン壁紙")
                    .cssClass("theme-modern")
                    .rarity("N")
                    .build());

            /* ============================
               初期家具（N）
            ============================ */
            roomItemRepository.save(RoomItemEntity.builder()
                    .type("FURNITURE")
                    .name("観葉植物")
                    .cssClass("furniture-plant-01")
                    .rarity("N")
                    .build());

            roomItemRepository.save(RoomItemEntity.builder()
                    .type("FURNITURE")
                    .name("木のテーブル")
                    .cssClass("furniture-desk-01")
                    .rarity("N")
                    .build());

            roomItemRepository.save(RoomItemEntity.builder()
                    .type("FURNITURE")
                    .name("シンプルチェア")
                    .cssClass("furniture-chair-01")
                    .rarity("N")
                    .build());

            /* ============================
               ガチャ排出テーマ（R / SR / SSR）
            ============================ */
            roomItemRepository.save(RoomItemEntity.builder()
                    .type("THEME")
                    .name("森の壁紙")
                    .cssClass("theme-forest")
                    .rarity("R")
                    .build());

            roomItemRepository.save(RoomItemEntity.builder()
                    .type("THEME")
                    .name("夜空の壁紙")
                    .cssClass("theme-night")
                    .rarity("SR")
                    .build());

            roomItemRepository.save(RoomItemEntity.builder()
                    .type("THEME")
                    .name("サイバー壁紙")
                    .cssClass("theme-cyber")
                    .rarity("SSR")
                    .build());

            roomItemRepository.save(RoomItemEntity.builder()
                    .type("THEME")
                    .name("ホログラム壁紙")
                    .cssClass("theme-hologram")
                    .rarity("SSR")
                    .build());

            roomItemRepository.save(RoomItemEntity.builder()
                    .type("THEME")
                    .name("魔法陣壁紙")
                    .cssClass("theme-magic")
                    .rarity("SSR")
                    .build());

            /* ============================
               ▼ ここから追加：家具 Rレア 20件
               chair / desk / bed / plant の4種 × 5色
            ============================ */

            // ▼ 色一覧（自然系）
            String[] colors = {
                "brown", "dark_brown", "white", "gray", "olive"
            };

            /* ----------------------------
               チェア（chair）5色
            ---------------------------- */
            for (String c : colors) {
                roomItemRepository.save(RoomItemEntity.builder()
                        .type("FURNITURE")
                        .name("チェア（" + convertColorName(c) + "）")
                        .cssClass("furniture-chair-01-" + c)
                        .rarity("R")
                        .build());
            }

            /* ----------------------------
               デスク（desk）5色
            ---------------------------- */
            for (String c : colors) {
                roomItemRepository.save(RoomItemEntity.builder()
                        .type("FURNITURE")
                        .name("デスク（" + convertColorName(c) + "）")
                        .cssClass("furniture-desk-01-" + c)
                        .rarity("R")
                        .build());
            }

            /* ----------------------------
               ベッド（bed）5色
            ---------------------------- */
            for (String c : colors) {
                roomItemRepository.save(RoomItemEntity.builder()
                        .type("FURNITURE")
                        .name("ベッド（" + convertColorName(c) + "）")
                        .cssClass("furniture-bed-01-" + c)
                        .rarity("R")
                        .build());
            }

            /* ----------------------------
               観葉植物（plant）5色
            ---------------------------- */
            for (String c : colors) {
                roomItemRepository.save(RoomItemEntity.builder()
                        .type("FURNITURE")
                        .name("観葉植物（" + convertColorName(c) + "鉢）")
                        .cssClass("furniture-plant-01-" + c)
                        .rarity("R")
                        .build());
            }
        }
    }

    /* ============================
       色名変換（日本語表示用）
    ============================ */
    private String convertColorName(String color) {
        return switch (color) {
            case "brown" -> "ブラウン";
            case "dark_brown" -> "ダークブラウン";
            case "white" -> "ホワイト";
            case "gray" -> "グレー";
            case "olive" -> "オリーブ";
            default -> color;
        };
    }
}
