package com.example.salary.salaryconfirm.validation;

import java.io.Serializable;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SalaryConfirmRequiredValidator
        implements ConstraintValidator<SalaryConfirmRequired, Serializable> {

    @Override
    public boolean isValid(Serializable value, ConstraintValidatorContext context) {

        if (value == null) {
            return false;
        }

        if (value instanceof Integer) {
            return (Integer) value >= 0;
        }

        return true;
    }
}