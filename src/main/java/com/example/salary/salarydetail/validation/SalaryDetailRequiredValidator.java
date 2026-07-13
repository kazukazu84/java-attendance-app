package com.example.salary.salarydetail.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SalaryDetailRequiredValidator implements ConstraintValidator<SalaryDetailRequired, Object> {
	
	@Override
	public void initialize(SalaryDetailRequired constraintAnnotation) {
	}
	
	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		return true;
	}
}
