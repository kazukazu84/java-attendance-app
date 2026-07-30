package com.example.adminshift.entity;


import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;



class ShiftApplicationEventTest {



    /**
     * 表示期間取得
     */
    @Test
    void getDisplayName_正常(){

        ShiftApplicationEvent event =
                new ShiftApplicationEvent();


        event.setTargetStartDate(
                LocalDate.of(2026,8,1)
        );


        event.setTargetEndDate(
                LocalDate.of(2026,8,31)
        );



        assertEquals(
                "2026/08/01～2026/08/31",
                event.getDisplayName()
        );

    }





    /**
     * 開始日null
     */
    @Test
    void getDisplayName_開始日null(){

        ShiftApplicationEvent event =
                new ShiftApplicationEvent();


        event.setTargetStartDate(null);


        event.setTargetEndDate(
                LocalDate.of(2026,8,31)
        );



        assertEquals(
                "",
                event.getDisplayName()
        );

    }





    /**
     * 終了日null
     */
    @Test
    void getDisplayName_終了日null(){

        ShiftApplicationEvent event =
                new ShiftApplicationEvent();


        event.setTargetStartDate(
                LocalDate.of(2026,8,1)
        );


        event.setTargetEndDate(null);



        assertEquals(
                "",
                event.getDisplayName()
        );

    }





    /**
     * 受付前
     *
     * 現在日より未来
     */
    @Test
    void getStatusName_受付前(){

        ShiftApplicationEvent event =
                new ShiftApplicationEvent();


        event.setApplicationStartDate(
                LocalDate.now().plusDays(10)
        );


        event.setApplicationEndDate(
                LocalDate.now().plusDays(20)
        );



        assertEquals(
                "受付前",
                event.getStatusName()
        );

    }





    /**
     * 受付中
     */
    @Test
    void getStatusName_受付中(){

        ShiftApplicationEvent event =
                new ShiftApplicationEvent();


        event.setApplicationStartDate(
                LocalDate.now().minusDays(10)
        );


        event.setApplicationEndDate(
                LocalDate.now().plusDays(10)
        );



        assertEquals(
                "受付中",
                event.getStatusName()
        );

    }





    /**
     * 受付終了
     */
    @Test
    void getStatusName_受付終了(){

        ShiftApplicationEvent event =
                new ShiftApplicationEvent();


        event.setApplicationStartDate(
                LocalDate.now().minusDays(20)
        );


        event.setApplicationEndDate(
                LocalDate.now().minusDays(10)
        );



        assertEquals(
                "受付終了",
                event.getStatusName()
        );

    }





    /**
     * CSS 受付前
     */
    @Test
    void getStatusCssClass_受付前(){

        ShiftApplicationEvent event =
                new ShiftApplicationEvent();


        event.setApplicationStartDate(
                LocalDate.now().plusDays(10)
        );


        event.setApplicationEndDate(
                LocalDate.now().plusDays(20)
        );



        assertEquals(
                "status-before",
                event.getStatusCssClass()
        );

    }





    /**
     * CSS 受付中
     */
    @Test
    void getStatusCssClass_受付中(){

        ShiftApplicationEvent event =
                new ShiftApplicationEvent();


        event.setApplicationStartDate(
                LocalDate.now().minusDays(10)
        );


        event.setApplicationEndDate(
                LocalDate.now().plusDays(10)
        );



        assertEquals(
                "status-open",
                event.getStatusCssClass()
        );

    }





    /**
     * CSS 受付終了
     */
    @Test
    void getStatusCssClass_受付終了(){

        ShiftApplicationEvent event =
                new ShiftApplicationEvent();


        event.setApplicationStartDate(
                LocalDate.now().minusDays(20)
        );


        event.setApplicationEndDate(
                LocalDate.now().minusDays(10)
        );



        assertEquals(
                "status-closed",
                event.getStatusCssClass()
        );

    }





    /**
     * 受付日null
     */
    @Test
    void getStatusName_受付日null(){

        ShiftApplicationEvent event =
                new ShiftApplicationEvent();


        event.setApplicationStartDate(null);


        event.setApplicationEndDate(
                LocalDate.now()
        );



        assertEquals(
                "",
                event.getStatusName()
        );

    }





    /**
     * CSS null
     */
    @Test
    void getStatusCssClass_受付日null(){

        ShiftApplicationEvent event =
                new ShiftApplicationEvent();


        event.setApplicationStartDate(null);


        event.setApplicationEndDate(null);



        assertEquals(
                "",
                event.getStatusCssClass()
        );

    }

}