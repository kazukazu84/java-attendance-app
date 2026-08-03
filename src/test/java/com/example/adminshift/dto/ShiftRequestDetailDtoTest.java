package com.example.adminshift.dto;


import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;


/**
 * ShiftRequestDetailDto 単体テスト
 */
class ShiftRequestDetailDtoTest {


    /**
     * デフォルトコンストラクタ確認
     */
    @Test
    void constructor_デフォルト生成確認() {


        ShiftRequestDetailDto dto =
                new ShiftRequestDetailDto();



        assertNull(
                dto.getWorkDate()
        );


        assertNull(
                dto.getIsAvailable()
        );


        assertNull(
                dto.getRequestedStartTime()
        );


        assertNull(
                dto.getRequestedEndTime()
        );

    }





    /**
     * AllArgsConstructor確認
     */
    @Test
    void constructor_全項目指定生成確認() {


        LocalDate date =
                LocalDate.of(2026, 8, 1);


        LocalTime start =
                LocalTime.of(9, 0);


        LocalTime end =
                LocalTime.of(18, 0);



        ShiftRequestDetailDto dto =
                new ShiftRequestDetailDto(
                        date,
                        true,
                        start,
                        end
                );



        assertEquals(
                date,
                dto.getWorkDate()
        );


        assertEquals(
                true,
                dto.getIsAvailable()
        );


        assertEquals(
                start,
                dto.getRequestedStartTime()
        );


        assertEquals(
                end,
                dto.getRequestedEndTime()
        );

    }





    /**
     * 日付表示正常
     */
    @Test
    void getFormattedWorkDate_正常() {


        ShiftRequestDetailDto dto =
                new ShiftRequestDetailDto();


        dto.setWorkDate(
                LocalDate.of(2026, 8, 1)
        );



        assertEquals(
                "2026/08/01",
                dto.getFormattedWorkDate()
        );

    }





    /**
     * 日付null
     */
    @Test
    void getFormattedWorkDate_nullの場合() {


        ShiftRequestDetailDto dto =
                new ShiftRequestDetailDto();



        assertEquals(
                "",
                dto.getFormattedWorkDate()
        );

    }





    /**
     * 出勤可能
     */
    @Test
    void getAvailabilityText_trueの場合() {


        ShiftRequestDetailDto dto =
                new ShiftRequestDetailDto();


        dto.setIsAvailable(true);



        assertEquals(
                "〇",
                dto.getAvailabilityText()
        );

    }





    /**
     * 休み
     */
    @Test
    void getAvailabilityText_falseの場合() {


        ShiftRequestDetailDto dto =
                new ShiftRequestDetailDto();


        dto.setIsAvailable(false);



        assertEquals(
                "×",
                dto.getAvailabilityText()
        );

    }





    /**
     * nullの場合
     */
    @Test
    void getAvailabilityText_nullの場合() {


        ShiftRequestDetailDto dto =
                new ShiftRequestDetailDto();



        assertEquals(
                "×",
                dto.getAvailabilityText()
        );

    }





    /**
     * 開始時間表示
     */
    @Test
    void getStartTimeText_出勤時間あり() {


        ShiftRequestDetailDto dto =
                new ShiftRequestDetailDto();


        dto.setIsAvailable(true);


        dto.setRequestedStartTime(
                LocalTime.of(9, 5)
        );



        assertEquals(
                "09:05",
                dto.getStartTimeText()
        );

    }





    /**
     * 開始時間なし
     */
    @Test
    void getStartTimeText_時間nullの場合() {


        ShiftRequestDetailDto dto =
                new ShiftRequestDetailDto();


        dto.setIsAvailable(true);



        assertEquals(
                "-",
                dto.getStartTimeText()
        );

    }





    /**
     * 休みの場合
     */
    @Test
    void getStartTimeText_休みの場合() {


        ShiftRequestDetailDto dto =
                new ShiftRequestDetailDto();


        dto.setIsAvailable(false);


        dto.setRequestedStartTime(
                LocalTime.of(9,0)
        );



        assertEquals(
                "-",
                dto.getStartTimeText()
        );

    }





    /**
     * 終了時間表示
     */
    @Test
    void getEndTimeText_退勤時間あり() {


        ShiftRequestDetailDto dto =
                new ShiftRequestDetailDto();


        dto.setIsAvailable(true);


        dto.setRequestedEndTime(
                LocalTime.of(18,30)
        );



        assertEquals(
                "18:30",
                dto.getEndTimeText()
        );

    }





    /**
     * 終了時間なし
     */
    @Test
    void getEndTimeText_時間nullの場合() {


        ShiftRequestDetailDto dto =
                new ShiftRequestDetailDto();


        dto.setIsAvailable(true);



        assertEquals(
                "-",
                dto.getEndTimeText()
        );

    }





    /**
     * 休みの場合
     */
    @Test
    void getEndTimeText_休みの場合() {


        ShiftRequestDetailDto dto =
                new ShiftRequestDetailDto();


        dto.setIsAvailable(false);


        dto.setRequestedEndTime(
                LocalTime.of(18,0)
        );



        assertEquals(
                "-",
                dto.getEndTimeText()
        );

    }

}