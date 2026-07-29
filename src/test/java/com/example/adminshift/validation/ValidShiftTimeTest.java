package com.example.adminshift.validation;


import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;



class ValidShiftTimeTest {


    @Test
    void annotationデフォルトメッセージ確認() {


        ValidShiftTime annotation =
                Dummy.class.getAnnotation(
                        ValidShiftTime.class
                );


        assertEquals(
                "出勤の場合は出勤時間と退勤時間の両方を入力してください。",
                annotation.message()
        );

    }



    @ValidShiftTime
    static class Dummy {

    }

}