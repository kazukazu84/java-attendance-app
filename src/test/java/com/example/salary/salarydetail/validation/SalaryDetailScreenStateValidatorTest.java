package com.example.salary.salarydetail.validation;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.salary.common.validation.ScreenStateGroup;
import com.example.salary.salarydetail.dto.SalaryDetailDto;

public class SalaryDetailScreenStateValidatorTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void 初期表示かつ遷移元がsalaryConfirmなら正常() {

        SalaryDetailDto dto = new SalaryDetailDto(
                2024,
                5,
                120.5,
                1200,
                150000,
                500,
                149500
        );

        dto.setInitialDisplay(true);
        dto.setFromScreen("salaryConfirm");

        Set<ConstraintViolation<SalaryDetailDto>> violations =
                validator.validate(dto, ScreenStateGroup.class);

        assertTrue(violations.isEmpty());
    }

    @Test
    void 初期表示でない場合はエラー() {

        SalaryDetailDto dto = new SalaryDetailDto(
                2024,
                5,
                120.5,
                1200,
                150000,
                500,
                149500
        );

        dto.setInitialDisplay(false);
        dto.setFromScreen("salaryConfirm");

        Set<ConstraintViolation<SalaryDetailDto>> violations =
                validator.validate(dto, ScreenStateGroup.class);

        assertFalse(violations.isEmpty());
    }

    @Test
    void 遷移元がsalaryConfirmでない場合はエラー() {

        SalaryDetailDto dto = new SalaryDetailDto(
                2024,
                5,
                120.5,
                1200,
                150000,
                500,
                149500
        );

        dto.setInitialDisplay(true);
        dto.setFromScreen("main");

        Set<ConstraintViolation<SalaryDetailDto>> violations =
                validator.validate(dto, ScreenStateGroup.class);

        assertFalse(violations.isEmpty());
    }
}
