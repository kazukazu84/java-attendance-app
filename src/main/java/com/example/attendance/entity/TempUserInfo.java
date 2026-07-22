//package com.example.attendance.entity;
//
//import java.time.LocalDate;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.Id;
//import jakarta.persistence.Table;
//
//import lombok.Data;
//
//@Entity
//@Table(name = "user_info")
//@Data
//public class TempUserInfo {
//
//	@Id
//	@Column(name = "user_id") // テーブルの物理名と合わせる
//	private String userId;
//
//    @Column(nullable = false)
//    private String password;
//
//    @Column(nullable = false)
//    private String userName;
//
//    @Column(nullable = false)
//    private String position;
//
//    @Column(nullable = false)
//    private Integer wageType;
//
//    @Column(nullable = false)
//    private LocalDate birthDate;
//
//    @Column(nullable = false)
//    private Integer attendanceStatus; // 0: 退勤状態, 1: 出勤状態
//
//    @Column(nullable = false)
//    private Boolean isEmploymentInsurance;
//
//    @Column(nullable = false)
//    private Integer isActive;
//}