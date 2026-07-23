package com.example.salary.salaryconfirm.validation;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.salary.common.validation.ConsistencyGroup;
import com.example.salary.salaryconfirm.dto.SalaryConfirmDto;

public class SalaryConfirmConsistencyValidatorTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void 整合性が正常ならエラーなし() {

        SalaryConfirmDto dto = new SalaryConfirmDto(
                5,
                200000,
                1,
                2024
        );

        Set<ConstraintViolation<SalaryConfirmDto>> violations =
                validator.validate(dto, ConsistencyGroup.class);

        assertTrue(violations.isEmpty());
    }

    @Test
    void netSalaryが負ならエラー() {

        SalaryConfirmDto dto = new SalaryConfirmDto(
                5,
                -100,
                1,
                2024
        );

        Set<ConstraintViolation<SalaryConfirmDto>> violations =
                validator.validate(dto, ConsistencyGroup.class);

        assertFalse(violations.isEmpty());
    }

    @Test
    void 年度が範囲外ならエラー() {

        SalaryConfirmDto dto = new SalaryConfirmDto(
                5,
                200000,
                1,
                1500 // 範囲外
        );

        Set<ConstraintViolation<SalaryConfirmDto>> violations =
                validator.validate(dto, ConsistencyGroup.class);

        assertFalse(violations.isEmpty());
    }
}