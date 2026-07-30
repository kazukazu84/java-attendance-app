package com.example.salary.salarydetail.validation;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.salary.common.validation.ConsistencyGroup;
import com.example.salary.salarydetail.dto.SalaryDetailDto;

public class SalaryDetailConsistencyValidatorTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void 整合性が正常ならエラーなし() {

        SalaryDetailDto dto = new SalaryDetailDto(
                2024,
                5,
                100.0,
                1000,
                100000, // gross = 100 * 1000
                500,
                99500   // net = gross - insuranceFee
        );

        Set<ConstraintViolation<SalaryDetailDto>> violations =
                validator.validate(dto, ConsistencyGroup.class);

        assertTrue(violations.isEmpty());
    }

    @Test
    void 総支給額が計算と一致しなければエラー() {

        SalaryDetailDto dto = new SalaryDetailDto(
                2024,
                5,
                100.0,
                1000,
                99999, // gross が誤り
                500,
                99500
        );

        Set<ConstraintViolation<SalaryDetailDto>> violations =
                validator.validate(dto, ConsistencyGroup.class);

        assertFalse(violations.isEmpty());
    }

    @Test
    void 差引支給額が計算と一致しなければエラー() {

        SalaryDetailDto dto = new SalaryDetailDto(
                2024,
                5,
                100.0,
                1000,
                100000,
                500,
                99000 // net が誤り
        );

        Set<ConstraintViolation<SalaryDetailDto>> violations =
                validator.validate(dto, ConsistencyGroup.class);

        assertFalse(violations.isEmpty());
    }
}