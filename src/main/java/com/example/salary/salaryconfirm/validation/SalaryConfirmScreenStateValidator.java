package com.example.salary.salaryconfirm.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.example.salary.salaryconfirm.dto.SalaryConfirmDto;

public class SalaryConfirmScreenStateValidator
        implements ConstraintValidator<SalaryConfirmScreenStateValid, SalaryConfirmDto> {

    @Override
    public boolean isValid(SalaryConfirmDto dto, ConstraintValidatorContext context) {

        if (dto == null) return false;

        // 初期表示であること
        if (!dto.isInitialDisplay()) return false;

        // 遷移元画面が "main" であること
        if (!"main".equals(dto.getFromScreen())) return false;

        return true;
    }
}
