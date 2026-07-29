package com.example.adminshift.dto;


import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;


/**
 * ShiftRequestUserSummaryDto 単体テスト
 */
class ShiftRequestUserSummaryDtoTest {


    /**
     * デフォルトコンストラクタ確認
     */
    @Test
    void constructor_デフォルト生成確認() {


        ShiftRequestUserSummaryDto dto =
                new ShiftRequestUserSummaryDto();



        assertNull(
                dto.getUserId()
        );


        assertNull(
                dto.getUserName()
        );


        assertNull(
                dto.getSubmittedAt()
        );

    }





    /**
     * AllArgsConstructor確認
     */
    @Test
    void constructor_全項目指定生成確認() {


        LocalDateTime submittedAt =
                LocalDateTime.of(
                        2026,
                        7,
                        29,
                        10,
                        30
                );



        ShiftRequestUserSummaryDto dto =
                new ShiftRequestUserSummaryDto(
                        "U001",
                        "山田太郎",
                        submittedAt
                );



        assertEquals(
                "U001",
                dto.getUserId()
        );


        assertEquals(
                "山田太郎",
                dto.getUserName()
        );


        assertEquals(
                submittedAt,
                dto.getSubmittedAt()
        );

    }





    /**
     * 提出日時フォーマット
     */
    @Test
    void getFormattedSubmittedAt_日時あり() {


        ShiftRequestUserSummaryDto dto =
                new ShiftRequestUserSummaryDto();



        dto.setSubmittedAt(
                LocalDateTime.of(
                        2026,
                        7,
                        29,
                        9,
                        5
                )
        );



        assertEquals(
                "2026/07/29 09:05",
                dto.getFormattedSubmittedAt()
        );

    }





    /**
     * 提出日時null
     */
    @Test
    void getFormattedSubmittedAt_nullの場合() {


        ShiftRequestUserSummaryDto dto =
                new ShiftRequestUserSummaryDto();



        assertEquals(
                "-",
                dto.getFormattedSubmittedAt()
        );

    }





    /**
     * 提出済みステータス
     */
    @Test
    void getStatus_提出済みの場合() {


        ShiftRequestUserSummaryDto dto =
                new ShiftRequestUserSummaryDto();



        dto.setSubmittedAt(
                LocalDateTime.of(
                        2026,
                        7,
                        29,
                        10,
                        0
                )
        );



        assertEquals(
                "提出",
                dto.getStatus()
        );

    }





    /**
     * 未提出ステータス
     */
    @Test
    void getStatus_未提出の場合() {


        ShiftRequestUserSummaryDto dto =
                new ShiftRequestUserSummaryDto();



        assertEquals(
                "未提出",
                dto.getStatus()
        );

    }





    /**
     * 提出済み判定true
     */
    @Test
    void isSubmitted_提出済みの場合true() {


        ShiftRequestUserSummaryDto dto =
                new ShiftRequestUserSummaryDto();



        dto.setSubmittedAt(
                LocalDateTime.now()
        );



        assertTrue(
                dto.isSubmitted()
        );

    }





    /**
     * 未提出判定false
     */
    @Test
    void isSubmitted_未提出の場合false() {


        ShiftRequestUserSummaryDto dto =
                new ShiftRequestUserSummaryDto();



        assertFalse(
                dto.isSubmitted()
        );

    }





    /**
     * Setter Getter確認
     */
    @Test
    void setterGetter確認() {


        ShiftRequestUserSummaryDto dto =
                new ShiftRequestUserSummaryDto();



        LocalDateTime dateTime =
                LocalDateTime.of(
                        2026,
                        8,
                        1,
                        12,
                        0
                );



        dto.setUserId("U100");

        dto.setUserName("佐藤一郎");

        dto.setSubmittedAt(dateTime);



        assertEquals(
                "U100",
                dto.getUserId()
        );


        assertEquals(
                "佐藤一郎",
                dto.getUserName()
        );


        assertEquals(
                dateTime,
                dto.getSubmittedAt()
        );

    }

}