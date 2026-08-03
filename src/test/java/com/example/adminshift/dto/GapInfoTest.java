package com.example.adminshift.dto;


import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;


/**
 * GapInfo DTO 単体テスト
 */
class GapInfoTest {


    /**
     * デフォルトコンストラクタ確認
     */
    @Test
    void constructor_デフォルト生成確認() {


        GapInfo gapInfo =
                new GapInfo();


        assertNull(
                gapInfo.getStartDate()
        );


        assertNull(
                gapInfo.getEndDate()
        );

    }





    /**
     * AllArgsConstructor確認
     */
    @Test
    void constructor_全項目指定生成確認() {


        LocalDate start =
                LocalDate.of(2026, 8, 1);


        LocalDate end =
                LocalDate.of(2026, 8, 31);



        GapInfo gapInfo =
                new GapInfo(
                        start,
                        end
                );



        assertEquals(
                start,
                gapInfo.getStartDate()
        );


        assertEquals(
                end,
                gapInfo.getEndDate()
        );

    }





    /**
     * startDate null
     */
    @Test
    void getMessage_startDateがnullの場合空文字() {


        GapInfo gapInfo =
                new GapInfo(
                        null,
                        LocalDate.of(2026, 8, 31)
                );



        assertEquals(
                "",
                gapInfo.getMessage()
        );

    }





    /**
     * endDate null
     */
    @Test
    void getMessage_endDateがnullの場合空文字() {


        GapInfo gapInfo =
                new GapInfo(
                        LocalDate.of(2026, 8, 1),
                        null
                );



        assertEquals(
                "",
                gapInfo.getMessage()
        );

    }





    /**
     * 1日だけのGap
     */
    @Test
    void getMessage_開始日終了日同じ場合() {


        GapInfo gapInfo =
                new GapInfo(
                        LocalDate.of(2026, 8, 1),
                        LocalDate.of(2026, 8, 1)
                );



        assertEquals(
                "2026/08/01のイベントが生成されていません",
                gapInfo.getMessage()
        );

    }





    /**
     * 期間指定Gap
     */
    @Test
    void getMessage_期間指定の場合() {


        GapInfo gapInfo =
                new GapInfo(
                        LocalDate.of(2026, 8, 1),
                        LocalDate.of(2026, 8, 31)
                );



        assertEquals(
                "2026/08/01～2026/08/31のイベントが生成されていません",
                gapInfo.getMessage()
        );

    }





    /**
     * Setter Getter確認
     */
    @Test
    void getterSetter確認() {


        GapInfo gapInfo =
                new GapInfo();



        LocalDate start =
                LocalDate.of(2026, 9, 1);


        LocalDate end =
                LocalDate.of(2026, 9, 30);



        gapInfo.setStartDate(start);

        gapInfo.setEndDate(end);



        assertEquals(
                start,
                gapInfo.getStartDate()
        );


        assertEquals(
                end,
                gapInfo.getEndDate()
        );

    }

}