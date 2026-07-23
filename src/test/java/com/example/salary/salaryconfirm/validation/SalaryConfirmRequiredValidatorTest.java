package com.example.salary.salaryconfirm.validation;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.salary.common.validation.RequiredGroup;
import com.example.salary.salaryconfirm.dto.SalaryConfirmDto;

public class SalaryConfirmRequiredValidatorTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void 必須項目が全て正常ならエラーなし() {

        SalaryConfirmDto dto = new SalaryConfirmDto(
                5,      // targetMonth
                200000, // netSalary
                1,      // userId
                2024    // targetYear
        );

        Set<ConstraintViolation<SalaryConfirmDto>> violations =
                validator.validate(dto, RequiredGroup.class);

        assertTrue(violations.isEmpty());
    }

    @Test
    void 必須項目がnullならエラーになる() {

        SalaryConfirmDto dto = new SalaryConfirmDto(
                null,   // targetMonth
                200000,
                1,
                2024
        );

        Set<ConstraintViolation<SalaryConfirmDto>> violations =
                validator.validate(dto, RequiredGroup.class);

        assertFalse(violations.isEmpty());
    }
}