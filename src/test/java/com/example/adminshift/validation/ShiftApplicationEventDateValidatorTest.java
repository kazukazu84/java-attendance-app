package com.example.adminshift.validation;


import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import jakarta.validation.ConstraintValidatorContext;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.example.adminshift.form.UpdateShiftApplicationEventForm;


/**
 * ShiftApplicationEventDateValidator 単体テスト
 */
class ShiftApplicationEventDateValidatorTest {


    private final ShiftApplicationEventDateValidator validator =
            new ShiftApplicationEventDateValidator();



    /**
     * ConstraintValidatorContext作成
     */
    private ConstraintValidatorContext createContext() {

        ConstraintValidatorContext context =
                Mockito.mock(ConstraintValidatorContext.class);


        ConstraintValidatorContext.ConstraintViolationBuilder builder =
                Mockito.mock(
                    ConstraintValidatorContext.ConstraintViolationBuilder.class
                );


        ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext nodeBuilder =
                Mockito.mock(
                    ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext.class
                );


        Mockito.when(
                context.buildConstraintViolationWithTemplate(Mockito.anyString())
        )
        .thenReturn(builder);


        Mockito.when(
                builder.addPropertyNode(Mockito.anyString())
        )
        .thenReturn(nodeBuilder);


        Mockito.when(
                nodeBuilder.addConstraintViolation()
        )
        .thenReturn(context);


        return context;
    }





    /**
     * formがnull
     */
    @Test
    void isValid_formnullの場合true() {


        boolean result =
                validator.isValid(
                        null,
                        null
                );


        assertTrue(result);

    }





    /**
     * 日付がnull
     */
    @Test
    void isValid_日付nullの場合true() {


        UpdateShiftApplicationEventForm form =
                new UpdateShiftApplicationEventForm();



        boolean result =
                validator.isValid(
                        form,
                        createContext()
                );



        assertTrue(result);

    }





    /**
     * 正常ケース
     */
    @Test
    void isValid_正常な日付の場合true() {


        UpdateShiftApplicationEventForm form =
                createForm(
                        LocalDate.of(2026, 8, 1),
                        LocalDate.of(2026, 8, 31),
                        LocalDate.of(2026, 7, 1),
                        LocalDate.of(2026, 7, 20)
                );



        boolean result =
                validator.isValid(
                        form,
                        createContext()
                );



        assertTrue(result);

    }





    /**
     * 条件①
     * 対象開始日 > 対象終了日
     */
    @Test
    void isValid_対象期間開始日が終了日より後false() {


        UpdateShiftApplicationEventForm form =
                createForm(
                        LocalDate.of(2026, 8, 31),
                        LocalDate.of(2026, 8, 1),
                        LocalDate.of(2026, 7, 1),
                        LocalDate.of(2026, 7, 20)
                );



        boolean result =
                validator.isValid(
                        form,
                        createContext()
                );



        assertFalse(result);

    }





    /**
     * 条件②
     * 受付開始日 > 受付終了日
     */
    @Test
    void isValid_受付開始日が終了日より後false() {


        UpdateShiftApplicationEventForm form =
                createForm(
                        LocalDate.of(2026, 8, 1),
                        LocalDate.of(2026, 8, 31),
                        LocalDate.of(2026, 7, 20),
                        LocalDate.of(2026, 7, 1)
                );



        boolean result =
                validator.isValid(
                        form,
                        createContext()
                );



        assertFalse(result);

    }





    /**
     * 条件③
     * 受付開始日 >= 対象開始日
     */
    @Test
    void isValid_受付開始日が対象開始日以降false() {


        UpdateShiftApplicationEventForm form =
                createForm(
                        LocalDate.of(2026, 8, 10),
                        LocalDate.of(2026, 8, 31),
                        LocalDate.of(2026, 8, 10),
                        LocalDate.of(2026, 8, 11)
                );



        boolean result =
                validator.isValid(
                        form,
                        createContext()
                );



        assertFalse(result);

    }





    /**
     * 条件④
     * 受付終了日 >= 対象開始日
     */
    @Test
    void isValid_受付終了日が対象開始日以降false() {


        UpdateShiftApplicationEventForm form =
                createForm(
                        LocalDate.of(2026, 8, 10),
                        LocalDate.of(2026, 8, 31),
                        LocalDate.of(2026, 8, 1),
                        LocalDate.of(2026, 8, 10)
                );



        boolean result =
                validator.isValid(
                        form,
                        createContext()
                );



        assertFalse(result);

    }





    /**
     * Form生成
     */
    private UpdateShiftApplicationEventForm createForm(
            LocalDate targetStartDate,
            LocalDate targetEndDate,
            LocalDate applicationStartDate,
            LocalDate applicationEndDate) {


        UpdateShiftApplicationEventForm form =
                new UpdateShiftApplicationEventForm();


        form.setTargetStartDate(targetStartDate);

        form.setTargetEndDate(targetEndDate);

        form.setApplicationStartDate(applicationStartDate);

        form.setApplicationEndDate(applicationEndDate);


        return form;
    }

}