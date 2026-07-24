package com.example.salary.salaryconfirm.form;

import com.example.salary.salaryconfirm.validation.SalaryConfirmRequired;

import lombok.Data;

@Data
public class SalaryConfirmForm {

    @SalaryConfirmRequired
    private String userId;   // ★ Integer → String に変更

    @SalaryConfirmRequired
    private Integer targetYear;

    @SalaryConfirmRequired
    private Integer targetMonth;
}
