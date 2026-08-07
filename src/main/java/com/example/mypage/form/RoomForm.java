package com.example.mypage.form;

import lombok.Data;

@Data
public class RoomForm {

    private Long themeItemId;
    private String layoutPattern; // LEFT, CENTER, RIGHT, SCATTER
    private boolean avatarVisible;

    // ▼ 家具スロット（1〜6）
    private Long slotItemId_1;
    private Long slotItemId_2;
    private Long slotItemId_3;
    private Long slotItemId_4;
    private Long slotItemId_5;
    private Long slotItemId_6;

    // ▼ スロット番号で取得するヘルパー
    public Long getSlotItemId(int slot) {
        return switch (slot) {
            case 1 -> slotItemId_1;
            case 2 -> slotItemId_2;
            case 3 -> slotItemId_3;
            case 4 -> slotItemId_4;
            case 5 -> slotItemId_5;
            case 6 -> slotItemId_6;
            default -> null;
        };
    }

    // ▼ スロット番号でセットするヘルパー（★追加）
    public void setSlotItemId(int slot, Long value) {
        switch (slot) {
            case 1 -> slotItemId_1 = value;
            case 2 -> slotItemId_2 = value;
            case 3 -> slotItemId_3 = value;
            case 4 -> slotItemId_4 = value;
            case 5 -> slotItemId_5 = value;
            case 6 -> slotItemId_6 = value;
        }
    }
}
