package com.example.adminshift.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Users {

    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "password")
    private String password;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "position")
    private String position;

    @Column(name = "wage_type")
    private Integer wageType;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "attendance_status")
    private Integer attendanceStatus;

    @Column(name = "is_employment_insurance")
    private Boolean isEmploymentInsurance;

    @Column(name = "is_active")
    private Integer isActive;
}