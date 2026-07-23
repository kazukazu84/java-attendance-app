package com.example.salary.salarydetail.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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

    @Column(name = "user_id")
    private int userId;

    @Column(name = "target_year")
    private int targetYear;

    @Column(name = "target_month")
    private int targetMonth;

    @Column(name = "working_hours")
    private double workingHours;

    @Column(name = "applied_hourly_wage")
    private int appliedHourlyWage;

    @Column(name = "applied_employment_insurance")
    private boolean appliedEmploymentInsurance;

}