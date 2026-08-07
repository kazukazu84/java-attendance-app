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
@Table(name = "avatar_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvatarItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long itemId;

    @Column(name = "type", nullable = false)
    private String type; // BASE, EAR, EYE, FACE, BODY, ACCESSORY

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "css_class", nullable = false)
    private String cssClass;

    @Column(name = "rarity", nullable = false)
    private String rarity; // N, R, SR, SSR
}