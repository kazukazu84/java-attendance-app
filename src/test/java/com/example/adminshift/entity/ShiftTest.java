package com.example.adminshift.entity;


import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;



class ShiftTest {



    /**
     * 夜勤判定
     *
     * startTime > endTime
     * 例:
     * 22:00～05:00
     */
    @Test
    void isNightShift_夜勤の場合true(){

        Shift shift =
                new Shift();


        shift.setIsAvailable(1);


        shift.setStartTime(
                LocalTime.of(22,0)
        );


        shift.setEndTime(
                LocalTime.of(5,0)
        );



        assertTrue(
                shift.isNightShift()
        );

    }





    /**
     * 通常勤務
     *
     * 09:00～18:00
     */
    @Test
    void isNightShift_通常勤務の場合false(){

        Shift shift =
                new Shift();


        shift.setIsAvailable(1);


        shift.setStartTime(
                LocalTime.of(9,0)
        );


        shift.setEndTime(
                LocalTime.of(18,0)
        );



        assertFalse(
                shift.isNightShift()
        );

    }





    /**
     * 休みの場合
     *
     * isAvailable=0
     */
    @Test
    void isNightShift_休みの場合false(){

        Shift shift =
                new Shift();


        shift.setIsAvailable(0);


        shift.setStartTime(
                LocalTime.of(22,0)
        );


        shift.setEndTime(
                LocalTime.of(5,0)
        );



        assertFalse(
                shift.isNightShift()
        );

    }





    /**
     * 開始時間null
     */
    @Test
    void isNightShift_開始時間nullの場合false(){

        Shift shift =
                new Shift();


        shift.setIsAvailable(1);


        shift.setStartTime(null);


        shift.setEndTime(
                LocalTime.of(5,0)
        );



        assertFalse(
                shift.isNightShift()
        );

    }





    /**
     * 終了時間null
     */
    @Test
    void isNightShift_終了時間nullの場合false(){

        Shift shift =
                new Shift();


        shift.setIsAvailable(1);


        shift.setStartTime(
                LocalTime.of(22,0)
        );


        shift.setEndTime(null);



        assertFalse(
                shift.isNightShift()
        );

    }





    /**
     * 同一時刻の場合
     *
     * startTime.isAfter()
     * がfalseになることを確認
     */
    @Test
    void isNightShift_開始終了同時刻の場合false(){

        Shift shift =
                new Shift();


        shift.setIsAvailable(1);


        shift.setStartTime(
                LocalTime.of(9,0)
        );


        shift.setEndTime(
                LocalTime.of(9,0)
        );



        assertFalse(
                shift.isNightShift()
        );

    }





    /**
     * Entity項目設定確認
     */
    @Test
    void getterSetter確認(){

        Shift shift =
                new Shift();


        shift.setId(1);


        shift.setEventId(10);


        shift.setUserId("U001");


        shift.setShiftDate(
                LocalDate.of(2026,8,1)
        );


        shift.setStartTime(
                LocalTime.of(9,0)
        );


        shift.setEndTime(
                LocalTime.of(18,0)
        );


        shift.setMemo("備考");


        shift.setIsAvailable(1);



        assertEquals(
                1,
                shift.getId()
        );


        assertEquals(
                10,
                shift.getEventId()
        );


        assertEquals(
                "U001",
                shift.getUserId()
        );


        assertEquals(
                LocalDate.of(2026,8,1),
                shift.getShiftDate()
        );


        assertEquals(
                LocalTime.of(9,0),
                shift.getStartTime()
        );


        assertEquals(
                LocalTime.of(18,0),
                shift.getEndTime()
        );


        assertEquals(
                "備考",
                shift.getMemo()
        );


        assertEquals(
                1,
                shift.getIsAvailable()
        );

    }





    /**
     * 初期値確認
     *
     * isAvailableは1
     */
    @Test
    void isAvailable_初期値確認(){

        Shift shift =
                new Shift();



        assertEquals(
                1,
                shift.getIsAvailable()
        );

    }

}