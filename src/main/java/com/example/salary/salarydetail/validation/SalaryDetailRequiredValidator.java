package com.example.salary.salarydetail.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SalaryDetailRequiredValidator
        implements ConstraintValidator<SalaryDetailRequired, Number> {

    @Override
    public boolean isValid(Number value, ConstraintValidatorContext context) {

        System.out.println("★ SalaryDetailRequiredValidator#isValid 呼び出し: value=" + value);

        if (value == null) {
            System.out.println("★ 値が null のため false を返します");
            return false;
        }

        double numeric = value.doubleValue();
        boolean result = numeric >= 0;

        System.out.println("★ 数値チェック: " + numeric + " >= 0 → " + result);

        return result;
    }
}
