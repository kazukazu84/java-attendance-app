package com.example.adminshift.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.adminshift.entity.Shift;
import com.example.adminshift.entity.ShiftApplicationEvent;

@SpringBootTest
@Transactional
class ShiftRepositoryTest {


    @Autowired
    private ShiftRepository shiftRepository;

    @Autowired
    private ShiftApplicationEventRepository eventRepository;


    private Integer testEventId;


    @BeforeEach
    void setup() {

        // 子テーブルから削除
        shiftRepository.deleteAll();

        // 親テーブル削除
        eventRepository.deleteAll();


        // テスト用イベント作成
        ShiftApplicationEvent event =
                new ShiftApplicationEvent();

        event.setTargetStartDate(
                LocalDate.of(2026, 8, 1)
        );

        event.setTargetEndDate(
                LocalDate.of(2026, 8, 31)
        );

        event.setApplicationStartDate(
                LocalDate.of(2026, 7, 20)
        );

        event.setApplicationEndDate(
                LocalDate.of(2026, 7, 25)
        );


        ShiftApplicationEvent saved =
                eventRepository.save(event);

        testEventId = saved.getEventId();
    }


    /**
     * findByEventId
     */
    @Test
    void findByEventId_取得できる() {


        Shift shift = createShift(
                testEventId,
                "U001",
                LocalDate.of(2026,8,1)
        );

        shiftRepository.save(shift);


        List<Shift> result =
                shiftRepository.findByEventId(testEventId);


        assertEquals(1,result.size());
        assertEquals(
                "U001",
                result.get(0).getUserId()
        );
    }


    /**
     * findByEventIdAndUserIdAndShiftDate
     */
    @Test
    void findByEventIdAndUserIdAndShiftDate_取得できる() {


        Shift shift = createShift(
                testEventId,
                "U001",
                LocalDate.of(2026,8,10)
        );

        shiftRepository.save(shift);


        Optional<Shift> result =
                shiftRepository
                .findByEventIdAndUserIdAndShiftDate(
                        testEventId,
                        "U001",
                        LocalDate.of(2026,8,10)
                );


        assertTrue(result.isPresent());

        assertEquals(
                "U001",
                result.get().getUserId()
        );
    }



    /**
     * 存在しない場合
     */
    @Test
    void findByEventIdAndUserIdAndShiftDate_存在しない場合() {


        Optional<Shift> result =
                shiftRepository
                .findByEventIdAndUserIdAndShiftDate(
                        testEventId,
                        "XXX",
                        LocalDate.of(2026,8,10)
                );


        assertTrue(result.isEmpty());
    }



    /**
     * deleteByEventId
     */
    @Test
    void deleteByEventId_削除できる() {


        shiftRepository.save(
                createShift(
                        testEventId,
                        "U001",
                        LocalDate.of(2026,8,1)
                )
        );


        shiftRepository.deleteByEventId(testEventId);


        List<Shift> result =
                shiftRepository.findByEventId(testEventId);


        assertEquals(
                0,
                result.size()
        );
    }



    /**
     * 期間外削除
     */
    @Test
    void deleteByEventIdAndShiftDateOutsideRange_期間外削除() {


        shiftRepository.save(
                createShift(
                        testEventId,
                        "U001",
                        LocalDate.of(2026,7,31)
                )
        );


        shiftRepository.save(
                createShift(
                        testEventId,
                        "U001",
                        LocalDate.of(2026,8,10)
                )
        );


        shiftRepository
        .deleteByEventIdAndShiftDateOutsideRange(
                testEventId,
                LocalDate.of(2026,8,1),
                LocalDate.of(2026,8,31)
        );


        List<Shift> result =
                shiftRepository.findByEventId(testEventId);


        assertEquals(
                1,
                result.size()
        );


        assertEquals(
                LocalDate.of(2026,8,10),
                result.get(0).getShiftDate()
        );
    }




    /**
     * 期間外データ存在チェック
     */
    @Test
    void existsByEventIdAndShiftDateOutsideRange_存在する場合() {


        shiftRepository.save(
                createShift(
                        testEventId,
                        "U001",
                        LocalDate.of(2026,9,1)
                )
        );


        boolean result =
                shiftRepository
                .existsByEventIdAndShiftDateOutsideRange(
                        testEventId,
                        LocalDate.of(2026,8,1),
                        LocalDate.of(2026,8,31)
                );


        assertTrue(result);
    }



    @Test
    void existsByEventIdAndShiftDateOutsideRange_存在しない場合() {


        shiftRepository.save(
                createShift(
                        testEventId,
                        "U001",
                        LocalDate.of(2026,8,10)
                )
        );


        boolean result =
                shiftRepository
                .existsByEventIdAndShiftDateOutsideRange(
                        testEventId,
                        LocalDate.of(2026,8,1),
                        LocalDate.of(2026,8,31)
                );


        assertFalse(result);
    }




    /**
     * findExistingShiftDatesByEventId
     */
    @Test
    void findExistingShiftDatesByEventId_日付一覧取得() {


        shiftRepository.save(
                createShift(
                        testEventId,
                        "U001",
                        LocalDate.of(2026,8,1)
                )
        );


        shiftRepository.save(
                createShift(
                        testEventId,
                        "U002",
                        LocalDate.of(2026,8,2)
                )
        );


        List<LocalDate> dates =
                shiftRepository
                .findExistingShiftDatesByEventId(
                        testEventId
                );


        assertEquals(
                2,
                dates.size()
        );

        assertTrue(
                dates.contains(
                        LocalDate.of(2026,8,1)
                )
        );

        assertTrue(
                dates.contains(
                        LocalDate.of(2026,8,2)
                )
        );
    }




    /**
     * テスト用Shift生成
     */
    private Shift createShift(
            Integer eventId,
            String userId,
            LocalDate date
    ){

        Shift shift = new Shift();

        shift.setEventId(eventId);
        shift.setUserId(userId);
        shift.setShiftDate(date);

        shift.setStartTime(
                LocalTime.of(9,0)
        );

        shift.setEndTime(
                LocalTime.of(18,0)
        );

        shift.setIsAvailable(1);

        shift.setMemo("テスト");

        return shift;
    }

}