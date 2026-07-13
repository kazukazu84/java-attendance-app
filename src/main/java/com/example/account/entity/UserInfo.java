package com.example.account.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

@Entity @Table(name = "user_info")
@Data
public class UserInfo {
    @Id
    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "user_name", nullable = false)
    private String userName;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "position", nullable = false)
    private Position position;
    
    @Column(name = "wage", nullable = false)
    private int wage;
    
    @Column(name = "birth_date", nullable = false)
    private Date birthDate;								
    
    @Column(name = "attendance_status", nullable = false)
    private int attendanceStatus;
    
    public String getAttendanceStatusStr() {
        return this.attendanceStatus == 1 ? "出勤" : "退勤";
    }
    
    @Column(name = "is_employment_insurance", nullable = false)
    private boolean isEmploymentInsurance;
    
 // 💡 雇用保険の表示ロジックをJavaに集約
    public String getEmploymentInsuranceStr() {
        return this.isEmploymentInsurance ? "対象" : "除外";
    }
    
    @Column(name = "is_active", nullable = false)
    private int isActive;
    
    public String getActiveStr() {
        return switch (this.isActive) {
            case 1 -> "有効";
            case 2 -> "無効";
            default -> "不明";
        };
    }
}