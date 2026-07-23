package com.example.salary.salarydetail.form;

import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class SalaryDetailForm {

    @NotNull(message = "ユーザーIDは必須です")
    private Integer userId;

    @NotNull(message = "対象年は必須です")
    private Integer targetYear;

    @NotNull(message = "対象月は必須です")
    private Integer targetMonth;
}