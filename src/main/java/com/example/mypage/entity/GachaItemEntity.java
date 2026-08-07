package com.example.mypage.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "gacha_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GachaItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gacha_item_id")
    private Long gachaItemId;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(name = "item_type", nullable = false)
    private String itemType; // AVATAR, ROOM

    @Column(name = "rarity", nullable = false)
    private String rarity; // N, R, SR, SSR

    @Column(name = "gacha_type", nullable = false)
    private String gachaType; // AVATAR, ROOM, THEME
}