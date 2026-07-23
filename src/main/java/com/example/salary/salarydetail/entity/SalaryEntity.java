package com.example.salary.salarydetail.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.example.account.entity.UserInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "salary")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalaryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "salary_id")
    private int salaryId;

    // ★ UserInfo と紐づける（型を String に統一）
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserInfo userInfo;

    @Column(name = "target_year", nullable = false)
    private int targetYear;

    @Column(name = "target_month", nullable = false)
    private int targetMonth;

    @Column(name = "working_hours", nullable = false)
    private double workingHours;

    @Column(name = "applied_hourly_wage", nullable = false)
    private int appliedHourlyWage;

    @Column(name = "applied_employment_insurance", nullable = false)
    private boolean appliedEmploymentInsurance;

    // ★ 給与計算結果（あなたの給与詳細画面に必要）
    @Column(name = "gross_salary", nullable = false)
    private int grossSalary;

    @Column(name = "insurance_fee", nullable = false)
    private int insuranceFee;

    @Column(name = "net_salary", nullable = false)
    private int netSalary;
}
