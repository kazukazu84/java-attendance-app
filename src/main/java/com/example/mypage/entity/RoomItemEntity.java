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
@Table(name = "room_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id") // ★ PK（家具・テーマの識別子）
    private Long itemId;

    @Column(name = "type", nullable = false)
    private String type; // ★ FURNITURE or THEME

    @Column(name = "name", nullable = false)
    private String name; // ★ 表示名（観葉植物など）

    @Column(name = "css_class", nullable = false)
    private String cssClass; // ★ CSS クラス（furniture-plant-01 など）

    @Column(name = "rarity", nullable = false)
    private String rarity; // ★ N, R, SR, SSR
}
