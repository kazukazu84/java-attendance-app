package com.example.salary.salarydetail.validation;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.salary.common.validation.RequiredGroup;
import com.example.salary.salarydetail.dto.SalaryDetailDto;

public class SalaryDetailRequiredValidatorTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void 必須項目が全て正常ならエラーなし() {

        SalaryDetailDto dto = new SalaryDetailDto(
                2024,
                5,
                120.5,
                1200,
                150000,
                500,
                149500
        );

        Set<ConstraintViolation<SalaryDetailDto>> violations =
                validator.validate(dto, RequiredGroup.class);

        assertTrue(violations.isEmpty());
    }

    @Test
    void 必須項目がnullならエラーになる() {

        SalaryDetailDto dto = new SalaryDetailDto(
                null,   // targetYear
                5,
                120.5,
                1200,
                150000,
                500,
                149500
        );

        Set<ConstraintViolation<SalaryDetailDto>> violations =
                validator.validate(dto, RequiredGroup.class);

        assertFalse(violations.isEmpty());
    }
}
