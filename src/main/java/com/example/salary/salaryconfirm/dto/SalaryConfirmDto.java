package com.example.salary.salaryconfirm.dto;

import com.example.salary.common.validation.ConsistencyGroup;
import com.example.salary.common.validation.RequiredGroup;
import com.example.salary.common.validation.ScreenStateGroup;
import com.example.salary.salaryconfirm.validation.SalaryConfirmConsistencyValid;
import com.example.salary.salaryconfirm.validation.SalaryConfirmRequired;
import com.example.salary.salaryconfirm.validation.SalaryConfirmScreenStateValid;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 給与確認画面 DTO（最新仕様）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SalaryConfirmConsistencyValid(groups = ConsistencyGroup.class)
@SalaryConfirmScreenStateValid(groups = ScreenStateGroup.class)
public class SalaryConfirmDto {

    @SalaryConfirmRequired(groups = RequiredGroup.class)
    private Integer targetMonth;

    @SalaryConfirmRequired(groups = RequiredGroup.class)
    private Integer netSalary;

    @SalaryConfirmRequired(groups = RequiredGroup.class)
    private String userId;   // ★ Integer → String に変更

    @SalaryConfirmRequired(groups = RequiredGroup.class)
    private Integer targetYear;

    private boolean initialDisplay;
    private String fromScreen;

    // ★ Service 用コンストラクタ（画面状態は Controller がセット）
    public SalaryConfirmDto(
            Integer targetMonth,
            Integer netSalary,
            String userId,        // ★ String に変更
            Integer targetYear
    ) {
        this.targetMonth = targetMonth;
        this.netSalary = netSalary;
        this.userId = userId;
        this.targetYear = targetYear;

        this.initialDisplay = false;
        this.fromScreen = null;
    }
}
