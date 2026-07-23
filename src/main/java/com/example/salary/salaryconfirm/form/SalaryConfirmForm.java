package com.example.salary.salaryconfirm.form;

import com.example.salary.salaryconfirm.validation.SalaryConfirmRequired;

import lombok.Data;

@Data
public class SalaryConfirmForm {

    @SalaryConfirmRequired
    private Integer userId;

    @SalaryConfirmRequired
    private Integer targetYear;

    @SalaryConfirmRequired
    private Integer targetMonth;
}