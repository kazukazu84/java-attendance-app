package com.example.salary.salarydetail.validation;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.salary.common.validation.ConsistencyGroup;
import com.example.salary.common.validation.RequiredGroup;
import com.example.salary.common.validation.ScreenStateGroup;
import com.example.salary.salarydetail.dto.SalaryDetailDto;

public class SalaryDetailAllValidatorTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void 全Validatorが正常に通過する() {

        SalaryDetailDto dto = new SalaryDetailDto(
                2024,
                5,
                100.0,
                1000,
                100000, // gross = 100 * 1000
                500,
                99500   // net = gross - insuranceFee
        );

        dto.setInitialDisplay(true);
        dto.setFromScreen("salaryConfirm");

        // Required
        assertTrue(validator.validate(dto, RequiredGroup.class).isEmpty());

        // ScreenState
        assertTrue(validator.validate(dto, ScreenStateGroup.class).isEmpty());

        // Consistency
        assertTrue(validator.validate(dto, ConsistencyGroup.class).isEmpty());
    }

    @Test
    void 整合性が崩れたら結合テストも失敗する() {

        SalaryDetailDto dto = new SalaryDetailDto(
                2024,
                5,
                100.0,
                1000,
                99999, // gross が誤り
                500,
                99500
        );

        dto.setInitialDisplay(true);
        dto.setFromScreen("salaryConfirm");

        Set<ConstraintViolation<SalaryDetailDto>> consistencyViolations =
                validator.validate(dto, ConsistencyGroup.class);

        assertFalse(consistencyViolations.isEmpty());
    }
}
