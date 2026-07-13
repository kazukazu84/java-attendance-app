package com.example.salary.salarydetail.entity;

import jakarta.persistence.Entity;
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
    private int salaryId;
    private int userId;
    private int targetYear;
    private int targetMonth;
    private double workingHours;
    private int wageId;
    private boolean appliedEmploymentInsurance;

}