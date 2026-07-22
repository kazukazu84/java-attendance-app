package com.example.adminshift.entity;



import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

/**
 * シフト情報を管理するエンティティクラス
 */
@Entity
@Table(name = "shifts")
@Data
public class Shift {

    /** シフトID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** イベントID */
    private Long eventId;

    /** ユーザー（従業員）ID */
    private Long userId;

    /** 勤務日 */
    private LocalDate shiftDate;

    /** 出勤予定時間 */
    private LocalTime startTime;

    /** 退勤予定時間 */
    private LocalTime endTime;

    /** 備考・メモ */
    private String memo;
}