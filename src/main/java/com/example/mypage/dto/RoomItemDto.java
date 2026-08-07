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
public class RoomItemDto {
    private Long itemId;
    private String type;
    private String name;
    private String cssClass;
    private String rarity;
    private boolean equipped;
    private List<Integer> slotIndex;

}