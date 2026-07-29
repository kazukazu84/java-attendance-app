package com.example.adminshift.validation;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import jakarta.validation.ConstraintValidatorContext;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.example.adminshift.entity.Shift;
import com.example.adminshift.form.ShiftForm;
import com.example.adminshift.repository.ShiftRepository;


/**
 * ShiftTimeValidator 単体テスト
 */
class ShiftTimeValidatorTest {


    private final ShiftRepository shiftRepository =
            Mockito.mock(ShiftRepository.class);


    private final ShiftTimeValidator validator =
            new ShiftTimeValidator(shiftRepository);




    /**
     * ConstraintValidatorContext生成
     */
    private ConstraintValidatorContext createContext() {

        ConstraintValidatorContext context =
                Mockito.mock(ConstraintValidatorContext.class);


        ConstraintValidatorContext.ConstraintViolationBuilder builder =
                Mockito.mock(
                        ConstraintValidatorContext.ConstraintViolationBuilder.class
                );


        ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext node =
                Mockito.mock(
                        ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext.class
                );


        when(
            context.buildConstraintViolationWithTemplate(anyString())
        )
        .thenReturn(builder);


        when(
            builder.addPropertyNode(anyString())
        )
        .thenReturn(node);


        when(
            node.addConstraintViolation()
        )
        .thenReturn(context);


        return context;
    }





    /**
     * form null
     */
    @Test
    void isValid_formnullの場合true() {


        assertTrue(
            validator.isValid(null, null)
        );

    }





    /**
     * 休みの場合
     */
    @Test
    void isValid_isAvailable0の場合true() {


        ShiftForm form =
                new ShiftForm();


        form.setIsAvailable(0);



        assertTrue(
            validator.isValid(
                    form,
                    createContext()
            )
        );

    }





    /**
     * rest=true
     */
    @Test
    void isValid_restの場合true() {


        ShiftForm form =
                new ShiftForm();


        form.setRest(true);



        assertTrue(
            validator.isValid(
                    form,
                    createContext()
            )
        );

    }





    /**
     * 開始時間なし
     */
    @Test
    void isValid_startTimeなしfalse() {


        ShiftForm form =
                createNormalForm();


        form.setStartTime(null);



        assertFalse(
            validator.isValid(
                    form,
                    createContext()
            )
        );

    }





    /**
     * 終了時間なし
     */
    @Test
    void isValid_endTimeなしfalse() {


        ShiftForm form =
                createNormalForm();


        form.setEndTime(null);



        assertFalse(
            validator.isValid(
                    form,
                    createContext()
            )
        );

    }





    /**
     * 通常勤務
     */
    @Test
    void isValid_通常勤務true() {


        ShiftForm form =
                createNormalForm();


        when(
            shiftRepository.findByEventIdAndUserIdAndShiftDate(
                    any(),
                    any(),
                    any()
            )
        )
        .thenReturn(Optional.empty());



        assertTrue(
            validator.isValid(
                    form,
                    createContext()
            )
        );

    }





    /**
     * 夜勤＋翌日シフトなし
     */
    @Test
    void isValid_夜勤翌日なしtrue() {


        ShiftForm form =
                createNormalForm();


        form.setStartTime(
                LocalTime.of(22,0)
        );


        form.setEndTime(
                LocalTime.of(5,0)
        );



        when(
            shiftRepository.findByEventIdAndUserIdAndShiftDate(
                    any(),
                    any(),
                    any()
            )
        )
        .thenReturn(Optional.empty());



        assertTrue(
            validator.isValid(
                    form,
                    createContext()
            )
        );

    }





    /**
     * 夜勤＋翌日開始時間重複
     */
    @Test
    void isValid_夜勤翌日重複false() {


        ShiftForm form =
                createNormalForm();


        form.setStartTime(
                LocalTime.of(22,0)
        );


        form.setEndTime(
                LocalTime.of(5,0)
        );



        Shift nextShift =
                new Shift();


        nextShift.setIsAvailable(1);


        nextShift.setStartTime(
                LocalTime.of(4,0)
        );



        when(
            shiftRepository.findByEventIdAndUserIdAndShiftDate(
                    any(),
                    any(),
                    any()
            )
        )
        .thenReturn(
                Optional.of(nextShift)
        );



        assertFalse(
            validator.isValid(
                    form,
                    createContext()
            )
        );

    }





    /**
     * 前日夜勤との重複
     */
    @Test
    void isValid_前日夜勤重複false() {


        ShiftForm form =
                createNormalForm();


        form.setStartTime(
                LocalTime.of(4,0)
        );


        form.setEndTime(
                LocalTime.of(12,0)
        );



        Shift prevShift =
                new Shift();


        prevShift.setIsAvailable(1);


        prevShift.setStartTime(
                LocalTime.of(22,0)
        );


        prevShift.setEndTime(
                LocalTime.of(5,0)
        );



        when(
            shiftRepository.findByEventIdAndUserIdAndShiftDate(
                    any(),
                    any(),
                    any()
            )
        )
        .thenReturn(
                Optional.of(prevShift)
        );



        assertFalse(
            validator.isValid(
                    form,
                    createContext()
            )
        );

    }





    /**
     * 前日夜勤だが重複なし
     */
    @Test
    void isValid_前日夜勤重複なしtrue() {


        ShiftForm form =
                createNormalForm();


        form.setStartTime(
                LocalTime.of(8,0)
        );


        form.setEndTime(
                LocalTime.of(17,0)
        );



        Shift prevShift =
                new Shift();


        prevShift.setIsAvailable(1);


        prevShift.setStartTime(
                LocalTime.of(22,0)
        );


        prevShift.setEndTime(
                LocalTime.of(5,0)
        );



        when(
            shiftRepository.findByEventIdAndUserIdAndShiftDate(
                    any(),
                    any(),
                    any()
            )
        )
        .thenReturn(
                Optional.of(prevShift)
        );



        assertTrue(
            validator.isValid(
                    form,
                    createContext()
            )
        );

    }





    /**
     * 通常勤務Form生成
     */
    private ShiftForm createNormalForm() {


        ShiftForm form =
                new ShiftForm();


        form.setEventId(1);

        form.setUserId("U001");


        form.setShiftDate(
                LocalDate.of(2026,8,1)
        );


        form.setIsAvailable(1);


        form.setStartTime(
                LocalTime.of(9,0)
        );


        form.setEndTime(
                LocalTime.of(18,0)
        );


        return form;

    }

}