package com.example.mypage.form;

import lombok.Data;

@Data
public class GachaForm {
    private String gachaType; // ガチャ種別（AVATAR / ROOM / THEME）
    private int count;        // 回数（1 or 10）
}
