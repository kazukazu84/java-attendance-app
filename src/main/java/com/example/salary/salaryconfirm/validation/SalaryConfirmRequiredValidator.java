package com.example.salary.salaryconfirm.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SalaryConfirmRequiredValidator implements ConstraintValidator<SalaryConfirmRequired, Object> {
	
	@Override
	public void initialize(SalaryConfirmRequired constraintAnnotation) {
	}
	
	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		return true;
	}
}
