package com.example.salary.salaryconfirm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalaryConfirmDto {

    private int targetMonth;     // 対象月
    private int totalSalary;     // 給与額（計算済み）

    private int userId;          // 詳細画面遷移用
    private int targetYear;      // 詳細画面遷移用
}