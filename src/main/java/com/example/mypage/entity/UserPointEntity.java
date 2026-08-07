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
@Table(name = "user_points")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPointEntity {

    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "current_points")
    private Integer currentPoints;

    @Column(name = "last_bonus_date")
    private LocalDateTime lastBonusDate;
}