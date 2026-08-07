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
@Table(name = "user_room_furniture")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRoomFurnitureEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_furniture_id")
    private Long roomFurnitureId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "furniture_item_id", nullable = false)
    private Long furnitureItemId;

    // ★ 新仕様：家具スロット番号（1〜6）
    @Column(name = "slot_index", nullable = false)
    private Integer slotIndex;
}
