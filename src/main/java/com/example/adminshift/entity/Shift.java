package com.example.adminshift.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

/**
 * シフト情報を管理するエンティティ
 */
@Entity
@Table(name = "shifts")
@Data
public class Shift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "event_id", nullable = false)
    private Integer eventId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "shift_date", nullable = false)
    private LocalDate shiftDate;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "memo")
    private String memo;

    /**
     * 出勤可能フラグ (0 = 休み, 1 = 出勤可能)
     */
    @Column(name = "is_available", nullable = false)
    private Integer isAvailable = 1;

    /**
     * 夜勤判定 (startTime > endTime かつ 出勤の場合)
     * Thymeleaf側で targetShift.nightShift として参照可能
     */
    public boolean isNightShift() {
        if (Integer.valueOf(0).equals(isAvailable) || startTime == null || endTime == null) {
            return false;
        }
        return startTime.isAfter(endTime);
    }
}