package com.example.adminshift.repository;


import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import com.example.adminshift.entity.ShiftApplicationEvent;


@SpringBootTest
@Transactional
class ShiftApplicationEventRepositoryTest {


    @Autowired
    private ShiftApplicationEventRepository repository;


    @Autowired
    private ShiftRepository shiftRepository;



    /**
     * 各テスト前にDB初期化
     */
    @BeforeEach
    void setup() {

        // 外部キー対策
        shiftRepository.deleteAll();

        repository.deleteAll();

    }





    /**
     * eventId降順取得
     */
    @Test
    void findAllByOrderByEventIdDesc_降順取得() {


        ShiftApplicationEvent event1 =
                createEvent(
                        LocalDate.of(2026,8,1),
                        LocalDate.of(2026,8,31));


        ShiftApplicationEvent event2 =
                createEvent(
                        LocalDate.of(2026,9,1),
                        LocalDate.of(2026,9,30));


        repository.save(event1);
        repository.save(event2);



        List<ShiftApplicationEvent> result =
                repository.findAllByOrderByEventIdDesc();



        assertEquals(2,result.size());


        assertTrue(
                result.get(0).getEventId()
                >
                result.get(1).getEventId()
        );

    }





    /**
     * 最新イベント取得
     */
    @Test
    void findTopByOrderByEventIdDesc_取得できる() {


        ShiftApplicationEvent event =
                createEvent(
                        LocalDate.of(2026,8,1),
                        LocalDate.of(2026,8,31));


        repository.save(event);



        Optional<ShiftApplicationEvent> result =
                repository.findTopByOrderByEventIdDesc();



        assertTrue(result.isPresent());

        assertEquals(
                event.getEventId(),
                result.get().getEventId()
        );

    }







    /**
     * ページ取得
     */
    @Test
    void findByTargetEndDateGreaterThanEqualOrderByTargetStartDateAsc_取得() {


        repository.save(
                createEvent(
                        LocalDate.of(2026,8,1),
                        LocalDate.of(2026,8,31))
        );


        repository.save(
                createEvent(
                        LocalDate.of(2026,9,1),
                        LocalDate.of(2026,9,30))
        );



        Page<ShiftApplicationEvent> result =
                repository
                .findByTargetEndDateGreaterThanEqualOrderByTargetStartDateAsc(
                        LocalDate.of(2026,8,1),
                        PageRequest.of(0,10)
                );



        assertEquals(
                2,
                result.getContent().size()
        );

    }







    /**
     * 対象終了日最大取得
     */
    @Test
    void findTopByOrderByTargetEndDateDesc_取得() {


        repository.save(
                createEvent(
                        LocalDate.of(2026,8,1),
                        LocalDate.of(2026,8,31))
        );


        repository.save(
                createEvent(
                        LocalDate.of(2026,9,1),
                        LocalDate.of(2026,9,30))
        );



        ShiftApplicationEvent result =
                repository.findTopByOrderByTargetEndDateDesc()
                .orElse(null);



        assertNotNull(result);


        assertEquals(
                LocalDate.of(2026,9,30),
                result.getTargetEndDate()
        );

    }







    /**
     * 重複あり
     */
    @Test
    void existsOverlappingEvent_重複ありtrue() {


        repository.save(
                createEvent(
                        LocalDate.of(2026,8,1),
                        LocalDate.of(2026,8,31))
        );



        boolean result =
                repository.existsOverlappingEvent(
                        LocalDate.of(2026,8,10),
                        LocalDate.of(2026,8,20)
                );



        assertTrue(result);

    }





    /**
     * 重複なし
     */
    @Test
    void existsOverlappingEvent_重複なしfalse() {


        repository.save(
                createEvent(
                        LocalDate.of(2026,8,1),
                        LocalDate.of(2026,8,31))
        );



        boolean result =
                repository.existsOverlappingEvent(
                        LocalDate.of(2026,9,1),
                        LocalDate.of(2026,9,30)
                );



        assertFalse(result);

    }







    /**
     * 自分自身除外
     */
    @Test
    void existsOverlappingEventExceptSelf_自分除外false() {


        ShiftApplicationEvent event =
                repository.save(
                        createEvent(
                                LocalDate.of(2026,8,1),
                                LocalDate.of(2026,8,31))
                );



        boolean result =
                repository.existsOverlappingEventExceptSelf(
                        event.getEventId(),
                        LocalDate.of(2026,8,1),
                        LocalDate.of(2026,8,31)
                );



        assertFalse(result);

    }







    /**
     * 開始日昇順
     */
    @Test
    void findAllByOrderByTargetStartDateAsc_昇順取得() {


        repository.save(
                createEvent(
                        LocalDate.of(2026,9,1),
                        LocalDate.of(2026,9,30))
        );


        repository.save(
                createEvent(
                        LocalDate.of(2026,8,1),
                        LocalDate.of(2026,8,31))
        );



        List<ShiftApplicationEvent> result =
                repository.findAllByOrderByTargetStartDateAsc();



        assertEquals(
                LocalDate.of(2026,8,1),
                result.get(0).getTargetStartDate()
        );

    }








    /**
     * 開始日＋ID順
     */
    @Test
    void findAllByOrderByTargetStartDateAscEventIdAsc_取得() {


        repository.save(
                createEvent(
                        LocalDate.of(2026,8,1),
                        LocalDate.of(2026,8,10))
        );


        repository.save(
                createEvent(
                        LocalDate.of(2026,8,1),
                        LocalDate.of(2026,8,20))
        );



        List<ShiftApplicationEvent> result =
                repository
                .findAllByOrderByTargetStartDateAscEventIdAsc();



        assertEquals(
                2,
                result.size()
        );


        assertTrue(
                result.get(0).getEventId()
                <
                result.get(1).getEventId()
        );

    }







    /**
     * 申請一覧対象取得
     */
    @Test
    void findTargetEventsForAdminList_取得() {


        LocalDate today =
                LocalDate.of(2026,7,29);



        //受付中
        ShiftApplicationEvent event1 =
                createEvent(
                        LocalDate.of(2026,8,1),
                        LocalDate.of(2026,8,31));


        event1.setApplicationStartDate(
                LocalDate.of(2026,7,1));

        event1.setApplicationEndDate(
                LocalDate.of(2026,7,30));



        repository.save(event1);




        List<ShiftApplicationEvent> result =
                repository.findTargetEventsForAdminList(today);



        assertEquals(
                1,
                result.size()
        );

    }







    /**
     * Entity生成
     */
    private ShiftApplicationEvent createEvent(
            LocalDate start,
            LocalDate end) {


        ShiftApplicationEvent event =
                new ShiftApplicationEvent();


        event.setTargetStartDate(start);

        event.setTargetEndDate(end);


        // NOT NULL対策
        event.setApplicationStartDate(
                start.minusDays(10)
        );


        event.setApplicationEndDate(
                start.minusDays(1)
        );


        return event;
    }

}