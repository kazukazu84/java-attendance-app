package com.example.mypage.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mypage_profile")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyPageProfileEntity {

    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "theme_color")
    private String themeColor;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}