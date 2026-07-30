package com.example.rail.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "rail_line_master")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RailLineMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // diainfo の ID（例：321, 537, 316, 285）
    @Column(nullable = false, unique = true)
    private String railCode;

    // 路線名（例：大阪メトロ御堂筋線）
    @Column(nullable = false)
    private String lineName;

    // 会社名（例：大阪メトロ、阪神、近鉄）
    @Column(nullable = false)
    private String companyName;

    // スクレイピング先 URL（例：https://transit.yahoo.co.jp/diainfo/321/0）
    @Column(nullable = false, unique = true)
    private String diainfoUrl;

    // エリアコード（例：6 = 近畿）
    @Column(nullable = false)
    private String areaCode;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
