package com.example.mypage.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_avatar_equipment")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAvatarEquipmentEntity {

    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "base_item_id")
    private Long baseItemId;

    @Column(name = "ear_item_id")
    private Long earItemId;

    @Column(name = "eye_item_id")
    private Long eyeItemId;

    @Column(name = "face_item_id")
    private Long faceItemId;

    @Column(name = "body_item_id")
    private Long bodyItemId;

    @Column(name = "accessory_item_id")
    private Long accessoryItemId;
}