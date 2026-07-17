package com.example.salary.salaryconfirm.validation;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.salary.common.validation.ScreenStateGroup;
import com.example.salary.salaryconfirm.dto.SalaryConfirmDto;

public class SalaryConfirmScreenStateValidatorTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void 初期表示かつ遷移元がmainなら正常() {

        SalaryConfirmDto dto = new SalaryConfirmDto(
                5,
                200000,
                1,
                2024
        );

        dto.setInitialDisplay(true);
        dto.setFromScreen("main");

        Set<ConstraintViolation<SalaryConfirmDto>> violations =
                validator.validate(dto, ScreenStateGroup.class);

        assertTrue(violations.isEmpty());
    }

    @Test
    void 初期表示でない場合はエラー() {

        SalaryConfirmDto dto = new SalaryConfirmDto(
                5,
                200000,
                1,
                2024
        );

        dto.setInitialDisplay(false);
        dto.setFromScreen("main");

        Set<ConstraintViolation<SalaryConfirmDto>> violations =
                validator.validate(dto, ScreenStateGroup.class);

        assertFalse(violations.isEmpty());
    }

    @Test
    void 遷移元がmainでない場合はエラー() {

        SalaryConfirmDto dto = new SalaryConfirmDto(
                5,
                200000,
                1,
                2024
        );

        dto.setInitialDisplay(true);
        dto.setFromScreen("salaryDetail");

        Set<ConstraintViolation<SalaryConfirmDto>> violations =
                validator.validate(dto, ScreenStateGroup.class);

        assertFalse(violations.isEmpty());
    }
}
