package com.example.salary.salarydetail.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.example.salary.salarydetail.dto.SalaryDetailDto;

public class SalaryDetailConsistencyValidator
        implements ConstraintValidator<SalaryDetailConsistencyValid, SalaryDetailDto> {

    @Override
    public boolean isValid(SalaryDetailDto dto, ConstraintValidatorContext context) {

        if (dto == null) {
            return false;
        }

        int expectedGross = (int) (dto.getWorkingHours() * dto.getAppliedHourlyWage());
        if (dto.getGrossSalary() != expectedGross) {
            return false;
        }

        int expectedNet = dto.getGrossSalary() - dto.getInsuranceFee();
        if (dto.getNetSalary() != expectedNet) {
            return false;
        }

        return true;
    }
}
