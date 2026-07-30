package com.example.rail.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "delay_info")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DelayInfoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private RailLineEntity railLine;

    private int delayMinutes;
    private String reason;          // ← そのまま保存
    private LocalDateTime occurredAt;
}
