package com.example.rail.form;

import lombok.Data;

@Data
public class RailUserProfileForm {

    // ログインユーザーID（String）
    private String userId;

    // 選択した路線コード（rail_line_master の railCode）
    private String favoriteRailCode;
}
