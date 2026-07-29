package com.example.adminshift.dto;


import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;


/**
 * ShiftApplicationEventSelectDto 単体テスト
 */
class ShiftApplicationEventSelectDtoTest {


    /**
     * デフォルトコンストラクタ確認
     */
    @Test
    void constructor_デフォルト生成確認() {


        ShiftApplicationEventSelectDto dto =
                new ShiftApplicationEventSelectDto();


        assertNull(
                dto.getEventId()
        );


        assertNull(
                dto.getDisplayName()
        );

    }





    /**
     * 全項目指定コンストラクタ確認
     */
    @Test
    void constructor_全項目指定生成確認() {


        ShiftApplicationEventSelectDto dto =
                new ShiftApplicationEventSelectDto(
                        1,
                        "2026/08/01～2026/08/31"
                );



        assertEquals(
                1,
                dto.getEventId()
        );


        assertEquals(
                "2026/08/01～2026/08/31",
                dto.getDisplayName()
        );

    }





    /**
     * Setter Getter確認
     */
    @Test
    void setterGetter確認() {


        ShiftApplicationEventSelectDto dto =
                new ShiftApplicationEventSelectDto();



        dto.setEventId(10);

        dto.setDisplayName(
                "イベント名"
        );



        assertEquals(
                10,
                dto.getEventId()
        );


        assertEquals(
                "イベント名",
                dto.getDisplayName()
        );

    }





    /**
     * null設定確認
     */
    @Test
    void null設定確認() {


        ShiftApplicationEventSelectDto dto =
                new ShiftApplicationEventSelectDto(
                        null,
                        null
                );



        assertNull(
                dto.getEventId()
        );


        assertNull(
                dto.getDisplayName()
        );

    }

}