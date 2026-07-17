package com.example.salary.salaryconfirm.validation;

import java.io.Serializable;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SalaryConfirmRequiredValidator
        implements ConstraintValidator<SalaryConfirmRequired, Serializable> {

    @Override
    public boolean isValid(Serializable value, ConstraintValidatorContext context) {

        System.out.println("★ SalaryConfirmRequiredValidator#isValid 呼び出し: value=" + value);

        if (value == null) {
            System.out.println("★ 値が null のため false を返します");
            return false;
        }

        if (value instanceof Integer) {
            boolean result = (Integer) value >= 0;
            System.out.println("★ Integer型チェック: " + value + " >= 0 → " + result);
            return result;
        }

        System.out.println("★ その他の型: " + value.getClass().getSimpleName() + " → true を返します");
        return true;
    }
}
