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
@Table(name = "user_room_state")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRoomStateEntity {

    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "theme_item_id")
    private Long themeItemId;

    @Column(name = "layout_pattern")
    private String layoutPattern;

    @Column(name = "avatar_visible")
    private Boolean avatarVisible;

}
