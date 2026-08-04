package com.example.salary.salarydetail.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.example.salary.salarydetail.dto.SalaryDetailDto;

public class SalaryDetailConsistencyValidator
        implements ConstraintValidator<SalaryDetailConsistencyValid, SalaryDetailDto> {

	@Override
	public boolean isValid(SalaryDetailDto dto, ConstraintValidatorContext context) {

	    if (dto == null) return false;

	    // ① 基本的な負数チェック（最低限）
	    if (dto.getWorkingHours() < 0 ||
	        dto.getGrossSalary() < 0 ||
	        dto.getInsuranceFee() < 0 ||
	        dto.getNetSalary() < 0) {
	        return false;
	    }

	    // ② 時給が正しいか（0より大きければOK）
	    if (dto.getAppliedHourlyWage() <= 0) {
	        return false;
	    }

	    // ④ netSalary = grossSalary - insuranceFee の最低限チェック
	    if (dto.getNetSalary() != dto.getGrossSalary() - dto.getInsuranceFee()) {
	        return false;
	    }

	    // ★ ⑤ それ以外の細かい計算チェックは行わない（最新仕様）
	    return true;
	}

}