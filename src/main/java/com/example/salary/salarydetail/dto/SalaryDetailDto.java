package com.example.salary.salarydetail.dto;

import com.example.salary.common.validation.ConsistencyGroup;
import com.example.salary.common.validation.RequiredGroup;
import com.example.salary.common.validation.ScreenStateGroup;
import com.example.salary.salarydetail.validation.SalaryDetailConsistencyValid;
import com.example.salary.salarydetail.validation.SalaryDetailRequired;
import com.example.salary.salarydetail.validation.SalaryDetailScreenStateValid;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 給与詳細画面 DTO（新構造）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SalaryDetailConsistencyValid(groups = ConsistencyGroup.class)
@SalaryDetailScreenStateValid(groups = ScreenStateGroup.class)
public class SalaryDetailDto {

    @SalaryDetailRequired(groups = RequiredGroup.class)
    private Integer targetYear;

    @SalaryDetailRequired(groups = RequiredGroup.class)
    private Integer targetMonth;

    @SalaryDetailRequired(groups = RequiredGroup.class)
    private Double workingHours;

    @SalaryDetailRequired(groups = RequiredGroup.class)
    private Integer appliedHourlyWage;

    @SalaryDetailRequired(groups = RequiredGroup.class)
    private Integer grossSalary;

    @SalaryDetailRequired(groups = RequiredGroup.class)
    private Integer insuranceFee;

    @SalaryDetailRequired(groups = RequiredGroup.class)
    private Integer netSalary;

    private boolean initialDisplay;
    private String fromScreen;

    // Service 用コンストラクタ（画面状態は Controller がセット）
    public SalaryDetailDto(
            Integer targetYear,
            Integer targetMonth,
            Double workingHours,
            Integer appliedHourlyWage,
            Integer grossSalary,
            Integer insuranceFee,
            Integer netSalary
    ) {
        System.out.println("★ SalaryDetailDto コンストラクタ呼び出し: " +
                "year=" + targetYear +
                ", month=" + targetMonth +
                ", hours=" + workingHours +
                ", wage=" + appliedHourlyWage +
                ", gross=" + grossSalary +
                ", insurance=" + insuranceFee +
                ", net=" + netSalary);

        this.targetYear = targetYear;
        this.targetMonth = targetMonth;
        this.workingHours = workingHours;
        this.appliedHourlyWage = appliedHourlyWage;
        this.grossSalary = grossSalary;
        this.insuranceFee = insuranceFee;
        this.netSalary = netSalary;

        this.initialDisplay = false;
        this.fromScreen = null;
    }
}
