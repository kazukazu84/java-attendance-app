package com.example.mypage.entity;

import java.time.LocalDateTime;

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
@Table(name = "gacha_results")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GachaResultEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_id")
    private Long resultId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "gacha_item_id", nullable = false)
    private Long gachaItemId;

    @Column(name = "obtained_at")
    private LocalDateTime obtainedAt;
}