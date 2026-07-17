package com.example.salary.salarydetail.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.example.salary.salarydetail.dto.SalaryDetailDto;

public class SalaryDetailScreenStateValidator
        implements ConstraintValidator<SalaryDetailScreenStateValid, SalaryDetailDto> {

    @Override
    public boolean isValid(SalaryDetailDto dto, ConstraintValidatorContext context) {

        if (dto == null) return false;

        // 初期表示であること
        if (!dto.isInitialDisplay()) return false;

        // 遷移元画面が "salaryConfirm" であること
        if (!"salaryConfirm".equals(dto.getFromScreen())) return false;

        return true;
    }
}
