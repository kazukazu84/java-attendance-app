package com.example.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvatarItemDto {
    private Long itemId;
    private String type;
    private String name;
    private String cssClass;
    private String rarity;
    private boolean equipped;
}