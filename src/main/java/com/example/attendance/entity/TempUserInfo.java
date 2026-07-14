package com.example.attendance.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "user_table") // 「user」はPostgreSQLの予約語（使えない名前）のため、安全のためにテーブル名を指定します
@Data
public class TempUserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false)
    private String position;

    @Column(nullable = false)
    private Integer wageType;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Column(nullable = false)
    private Integer attendanceStatus; // 0: 出勤状態, 1: 退勤状態

    @Column(nullable = false)
    private Boolean isEmploymentInsurance;

    @Column(nullable = false)
    private Integer isActive;
}