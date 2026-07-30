package com.example.adminshift.validation;


import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;


class ValidShiftApplicationEventDateTest {


    @Test
    void annotation設定確認() {


        ValidShiftApplicationEventDate annotation =
                Dummy.class.getAnnotation(
                        ValidShiftApplicationEventDate.class
                );


        assertEquals(
                "日付の入力内容に不備があります。",
                annotation.message()
        );

    }


    @ValidShiftApplicationEventDate
    static class Dummy {

    }

}