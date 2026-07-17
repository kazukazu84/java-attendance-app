package com.example.salary.salaryconfirm.validation;

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
import com.example.salary.salaryconfirm.dto.SalaryConfirmDto;

public class SalaryConfirmAllValidatorTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void 全Validatorが正常に通過する() {

        SalaryConfirmDto dto = new SalaryConfirmDto(
                5,        // targetMonth
                200000,   // netSalary
                1,        // userId
                2024      // targetYear
        );

        // Controller がセットする画面状態
        dto.setInitialDisplay(true);
        dto.setFromScreen("main");

        // Required
        assertTrue(validator.validate(dto, RequiredGroup.class).isEmpty());

        // ScreenState
        assertTrue(validator.validate(dto, ScreenStateGroup.class).isEmpty());

        // Consistency
        assertTrue(validator.validate(dto, ConsistencyGroup.class).isEmpty());
    }

    @Test
    void いずれかのValidatorが失敗したら結合テストも失敗する() {

        SalaryConfirmDto dto = new SalaryConfirmDto(
                5,
                -100,     // netSalary（整合性NG）
                1,
                2024
        );

        dto.setInitialDisplay(true);
        dto.setFromScreen("main");

        Set<ConstraintViolation<SalaryConfirmDto>> consistencyViolations =
                validator.validate(dto, ConsistencyGroup.class);

        assertFalse(consistencyViolations.isEmpty());
    }
}
