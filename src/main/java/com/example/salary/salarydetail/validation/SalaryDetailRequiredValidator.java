package com.example.salary.salarydetail.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SalaryDetailRequiredValidator
        implements ConstraintValidator<SalaryDetailRequired, Number> {

    @Override
    public boolean isValid(Number value, ConstraintValidatorContext context) {

        if (value == null) {
            return false;
        }

        double numeric = value.doubleValue();
        return numeric >= 0;
    }
}
