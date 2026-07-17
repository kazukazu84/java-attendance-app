package com.example.salary.salarydetail.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalaryDetailDto {

    private double workingHours;
    private int wageValue;
    private double grossSalary;
    private double employmentInsurance;
    private double netSalary;

}