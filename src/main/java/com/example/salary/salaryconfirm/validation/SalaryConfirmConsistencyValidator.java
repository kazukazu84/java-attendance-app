package com.example.salary.salaryconfirm.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.example.salary.salaryconfirm.dto.SalaryConfirmDto;

public class SalaryConfirmConsistencyValidator
        implements ConstraintValidator<SalaryConfirmConsistencyValid, SalaryConfirmDto> {

    @Override
    public boolean isValid(SalaryConfirmDto dto, ConstraintValidatorContext context) {

        if (dto == null) return false;

        // netSalary が負になることはありえない
        if (dto.getNetSalary() < 0) return false;

        // targetYear が 2000〜2100 の範囲内であること
        if (dto.getTargetYear() < 2000 || dto.getTargetYear() > 2100) return false;

        return true;
    }
}
