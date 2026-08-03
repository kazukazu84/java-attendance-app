package com.example.rail.entity;

import java.time.LocalDateTime;

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
@Table(name = "rail_line")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RailLineEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String company;
    private String lineName;

    private String statusText;   // ← そのまま保存
    private String detailText;   // ← そのまま保存
    private String updatedText;  // ← そのまま保存

    private LocalDateTime lastUpdated;
}
