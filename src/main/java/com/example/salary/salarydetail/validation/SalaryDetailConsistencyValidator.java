package com.example.salary.salarydetail.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.example.salary.salarydetail.dto.SalaryDetailDto;

public class SalaryDetailConsistencyValidator
        implements ConstraintValidator<SalaryDetailConsistencyValid, SalaryDetailDto> {

	@Override
	public boolean isValid(SalaryDetailDto dto, ConstraintValidatorContext context) {

	    System.out.println("★ SalaryDetailConsistencyValidator#isValid 呼び出し: dto=" + dto);

	    if (dto == null) {
	        System.out.println("★ dto が null のため false を返します");
	        return false;
	    }

	    int expectedGross = (int) (dto.getWorkingHours() * dto.getAppliedHourlyWage());
	    System.out.println("★ 総支給額チェック: actual=" + dto.getGrossSalary() +
	            ", expected=" + expectedGross);

	    if (dto.getGrossSalary() != expectedGross) {
	        System.out.println("★ 総支給額不一致のため false を返します");
	        return false;
	    }

	    int expectedNet = dto.getGrossSalary() - dto.getInsuranceFee();
	    System.out.println("★ 差引支給額チェック: actual=" + dto.getNetSalary() +
	            ", expected=" + expectedNet);

	    if (dto.getNetSalary() != expectedNet) {
	        System.out.println("★ 差引支給額不一致のため false を返します");
	        return false;
	    }

	    System.out.println("★ 全て一致したため true を返します");
	    return true;
	}

}
