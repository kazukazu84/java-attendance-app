package com.example.adminshift.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import com.example.adminshift.dto.MonthlyShiftSummaryDto;
import com.example.adminshift.entity.Shift;
import com.example.adminshift.entity.ShiftApplicationEvent;
import com.example.adminshift.entity.Users;
import com.example.adminshift.repository.ShiftApplicationEventRepository;
import com.example.adminshift.repository.ShiftRepository;
import com.example.adminshift.repository.UsersRepository;


@ExtendWith(MockitoExtension.class)
class ShiftCreateServiceTest {


    @InjectMocks
    private ShiftCreateService service;


    @Mock
    private ShiftApplicationEventRepository shiftApplicationEventRepository;


    @Mock
    private ShiftRepository shiftRepository;


    @Mock
    private UsersRepository usersRepository;



    /**
     * テスト用イベント作成
     */
    private ShiftApplicationEvent createEvent() {


        ShiftApplicationEvent event =
                new ShiftApplicationEvent();


        event.setEventId(1);


        event.setTargetStartDate(
                LocalDate.of(2026, 8, 1));


        event.setTargetEndDate(
                LocalDate.of(2026, 8, 3));


        event.setApplicationStartDate(
                LocalDate.of(2026, 7, 1));


        event.setApplicationEndDate(
                LocalDate.of(2026, 7, 20));


        return event;
    }



    /**
     * テスト用ユーザー作成
     */
    private Users createUser() {


        Users user =
                new Users();


        user.setUserId("user001");


        return user;
    }



    /**
     * テスト用シフト作成
     */
    private Shift createShift() {


        Shift shift =
                new Shift();


        shift.setId(1);


        shift.setEventId(1);


        shift.setUserId("user001");


        shift.setShiftDate(
                LocalDate.of(2026, 8, 1));


        shift.setStartTime(
                LocalTime.of(9, 0));


        shift.setEndTime(
                LocalTime.of(18, 0));


        shift.setIsAvailable(1);


        return shift;
    }



    /**
     * ==========================
     * イベント取得
     * ==========================
     */


    @Test
    @DisplayName("イベント一覧取得")
    void getEventList() {


        when(shiftApplicationEventRepository.findAll())
                .thenReturn(
                        List.of(createEvent()));


        List<ShiftApplicationEvent> result =
                service.getEventList();


        assertEquals(
                1,
                result.size());

    }



    @Test
    @DisplayName("イベント一覧取得時DBエラー")
    void getEventListException() {


        when(shiftApplicationEventRepository.findAll())
                .thenThrow(
                        new DataAccessException("error"){}
                );


        List<ShiftApplicationEvent> result =
                service.getEventList();


        assertTrue(
                result.isEmpty());

    }



    @Test
    @DisplayName("Repositoryがnullを返した場合")
    void getEventListNull() {


        when(shiftApplicationEventRepository.findAll())
                .thenReturn(null);


        List<ShiftApplicationEvent> result =
                service.getEventList();


        assertTrue(
                result.isEmpty());

    }



    @Test
    @DisplayName("未来イベント取得")
    void getOldestEvent() {


        ShiftApplicationEvent event =
                createEvent();


        when(shiftApplicationEventRepository.findAll())
                .thenReturn(
                        List.of(event));


        ShiftApplicationEvent result =
                service.getOldestEvent();


        assertNotNull(result);


        assertEquals(
                1,
                result.getEventId());

    }



    @Test
    @DisplayName("イベントなしの場合null")
    void getOldestEventEmpty() {


        when(shiftApplicationEventRepository.findAll())
                .thenReturn(
                        List.of());


        ShiftApplicationEvent result =
                service.getOldestEvent();


        assertNull(result);

    }



    @Test
    @DisplayName("イベント詳細取得")
    void getCurrentEvent() {


        when(shiftApplicationEventRepository.findById(1))
                .thenReturn(
                        Optional.of(createEvent()));


        ShiftApplicationEvent result =
                service.getCurrentEvent(1);


        assertNotNull(result);


        assertEquals(
                1,
                result.getEventId());

    }



    @Test
    @DisplayName("イベントIDがnull")
    void getCurrentEventNull() {


        ShiftApplicationEvent result =
                service.getCurrentEvent(null);


        assertNull(result);


        verify(
                shiftApplicationEventRepository,
                never())
                .findById(any());

    }



    @Test
    @DisplayName("イベントが存在しない場合")
    void getCurrentEventNotFound() {


        when(shiftApplicationEventRepository.findById(99))
                .thenReturn(
                        Optional.empty());


        ShiftApplicationEvent result =
                service.getCurrentEvent(99);


        assertNull(result);

    }



    @Test
    @DisplayName("イベント取得時DBエラー")
    void getCurrentEventException() {


        when(shiftApplicationEventRepository.findById(1))
                .thenThrow(
                        new DataAccessException("error"){}
                );


        ShiftApplicationEvent result =
                service.getCurrentEvent(1);


        assertNull(result);

    }



    /**
     * ==========================
     * シフト取得
     * ==========================
     */


    @Test
    @DisplayName("シフト一覧取得")
    void getShiftTable() {


        when(shiftRepository.findByEventId(1))
                .thenReturn(
                        List.of(createShift()));


        List<Shift> result =
                service.getShiftTable(1);


        assertEquals(
                1,
                result.size());

    }



    @Test
    @DisplayName("eventIdがnullの場合")
    void getShiftTableNull() {


        List<Shift> result =
                service.getShiftTable(null);


        assertTrue(
                result.isEmpty());


        verify(
                shiftRepository,
                never())
                .findByEventId(any());

    }



    @Test
    @DisplayName("シフト一覧取得時DBエラー")
    void getShiftTableException() {


        when(shiftRepository.findByEventId(1))
                .thenThrow(
                        new DataAccessException("error"){}
                );


        List<Shift> result =
                service.getShiftTable(1);


        assertTrue(
                result.isEmpty());

    }



    @Test
    @DisplayName("シフト詳細取得")
    void getShiftDetail() {


        when(shiftRepository.findById(1))
                .thenReturn(
                        Optional.of(createShift()));


        Shift result =
                service.getShiftDetail(1);


        assertNotNull(result);


        assertEquals(
                1,
                result.getId());

    }



    @Test
    @DisplayName("シフトID null")
    void getShiftDetailNull() {


        assertNull(
                service.getShiftDetail(null));

    }



    @Test
    @DisplayName("シフトなし")
    void getShiftDetailNotFound() {


        when(shiftRepository.findById(1))
                .thenReturn(
                        Optional.empty());


        Shift result =
                service.getShiftDetail(1);


        assertNull(result);

    }
    
    // ===============================
    // 第4回追加テスト part2
    // ===============================


    @Test
    @DisplayName("イベント開始日nullの場合取得対象外")
    void getOldestEventNullStartDate() {


        ShiftApplicationEvent event1 =
                createEvent();

        event1.setEventId(1);
        event1.setTargetStartDate(null);


        ShiftApplicationEvent event2 =
                createEvent();

        event2.setEventId(2);
        event2.setTargetStartDate(
                LocalDate.of(2026, 9, 1));


        when(shiftApplicationEventRepository.findAll())
                .thenReturn(
                        List.of(event1, event2));


        ShiftApplicationEvent result =
                service.getOldestEvent();


        assertNotNull(result);


        assertEquals(
                2,
                result.getEventId());

    }



    @Test
    @DisplayName("イベント終了日nullの場合")
    void getOldestEventNullEndDate() {


        ShiftApplicationEvent event =
                createEvent();


        event.setTargetEndDate(null);


        when(shiftApplicationEventRepository.findAll())
                .thenReturn(
                        List.of(event));


        ShiftApplicationEvent result =
                service.getOldestEvent();


        assertNotNull(result);


        assertEquals(
                1,
                result.getEventId());

    }



    @Test
    @DisplayName("イベントRepositoryがnullの場合")
    void getOldestEventRepositoryNull() {


        when(shiftApplicationEventRepository.findAll())
                .thenReturn(null);


        ShiftApplicationEvent result =
                service.getOldestEvent();


        assertNull(result);

    }




    @Test
    @DisplayName("複数未来イベントの場合はRepository先頭を取得")
    void getOldestEventRepositoryOrder() {

        ShiftApplicationEvent event1 =
                createEvent();
        event1.setEventId(10);
        event1.setTargetStartDate(
                LocalDate.now().plusDays(10));

        ShiftApplicationEvent event2 =
                createEvent();
        event2.setEventId(20);
        event2.setTargetStartDate(
                LocalDate.now().plusDays(1));

        when(shiftApplicationEventRepository.findAll())
                .thenReturn(
                        List.of(event1, event2));

        ShiftApplicationEvent result =
                service.getOldestEvent();

        assertNotNull(result);

        // ServiceはRepositoryの先頭の未来イベントを返す
        assertEquals(
                10,
                result.getEventId());
    }





    @Test
    @DisplayName("対象日付生成開始日null")
    void getTargetDateListStartNull() {


        ShiftApplicationEvent event =
                createEvent();


        event.setTargetStartDate(null);



        List<LocalDate> result =
                service.getTargetDateList(event);



        assertTrue(
                result.isEmpty());

    }





    @Test
    @DisplayName("対象日付生成終了日null")
    void getTargetDateListEndNull() {


        ShiftApplicationEvent event =
                createEvent();


        event.setTargetEndDate(null);



        List<LocalDate> result =
                service.getTargetDateList(event);



        assertTrue(
                result.isEmpty());

    }





    @Test
    @DisplayName("シフトRepositoryがnullを返す")
    void getShiftTableRepositoryNull() {


        when(shiftRepository.findByEventId(1))
                .thenReturn(null);



        List<Shift> result =
                service.getShiftTable(1);



        assertTrue(
                result.isEmpty());

    }





    @Test
    @DisplayName("シフト保存時null以外保存確認")
    void saveShiftNormalVerify() {


        Shift shift =
                createShift();


        service.saveShift(shift);



        verify(
                shiftRepository,
                times(1))
                .save(shift);

    }





    @Test
    @DisplayName("ユーザーなしで集計")
    void monthlySummaryEmptyUserList() {


        Map<String,List<MonthlyShiftSummaryDto>> result =
                service.getMonthlySummaryMap(
                        List.of(createShift()),
                        null);



        assertNotNull(result);


        assertTrue(
                result.isEmpty());

    }





    @Test
    @DisplayName("シフトリスト空の場合集計")
    void monthlySummaryEmptyShiftList() {


        Map<String,List<MonthlyShiftSummaryDto>> result =
                service.getMonthlySummaryMap(
                        List.of(),
                        List.of(createUser()));



        assertNotNull(
                result.get("user001"));



        assertTrue(
                result.get("user001").isEmpty());

    }





    @Test
    @DisplayName("勤務時間0分の場合")
    void monthlySummaryZeroMinute() {


        Shift shift =
                createShift();



        shift.setStartTime(
                LocalTime.of(10,0));


        shift.setEndTime(
                LocalTime.of(10,0));



        Map<String,List<MonthlyShiftSummaryDto>> result =
                service.getMonthlySummaryMap(
                        List.of(shift),
                        List.of(createUser()));



        assertEquals(
                0,
                result.get("user001")
                .get(0)
                .getTotalMinutes());

    }





    @Test
    @DisplayName("深夜勤務日跨ぎ確認")
    void monthlySummaryMidnightCross() {


        Shift shift =
                createShift();



        shift.setStartTime(
                LocalTime.of(23,30));


        shift.setEndTime(
                LocalTime.of(0,30));



        Map<String,List<MonthlyShiftSummaryDto>> result =
                service.getMonthlySummaryMap(
                        List.of(shift),
                        List.of(createUser()));



        assertEquals(
                60,
                result.get("user001")
                .get(0)
                .getTotalMinutes());

    }





    @Test
    @DisplayName("複数イベント一覧取得")
    void getEventListMultiple() {


        ShiftApplicationEvent event1 =
                createEvent();


        ShiftApplicationEvent event2 =
                createEvent();


        event2.setEventId(2);



        when(shiftApplicationEventRepository.findAll())
                .thenReturn(
                        List.of(event1,event2));



        List<ShiftApplicationEvent> result =
                service.getEventList();



        assertEquals(
                2,
                result.size());

    }





    @Test
    @DisplayName("シフト詳細Repository例外")
    void getShiftDetailRepositoryException() {


        when(shiftRepository.findById(1))
                .thenThrow(
                        new DataAccessException("DB Error"){}
                );



        Shift result =
                service.getShiftDetail(1);



        assertNull(result);

    }
    // ===============================
    // 第4回追加テスト part3
    // ===============================



    @Test
    @DisplayName("同じ開始日の場合は先頭イベントを取得")
    void getOldestEventSameStartDate() {

        ShiftApplicationEvent event1 = createEvent();
        event1.setEventId(1);
        event1.setTargetStartDate(LocalDate.now().plusDays(1));

        ShiftApplicationEvent event2 = createEvent();
        event2.setEventId(2);
        event2.setTargetStartDate(LocalDate.now().plusDays(1));

        when(shiftApplicationEventRepository.findAll())
                .thenReturn(List.of(event2, event1));

        ShiftApplicationEvent result =
                service.getOldestEvent();

        assertNotNull(result);

        // ServiceはfindFirst()なので先頭のevent2
        assertEquals(2, result.getEventId());
    }





    @Test
    @DisplayName("過去イベントのみの場合")
    void getOldestEventPastOnly() {


        ShiftApplicationEvent event =
                createEvent();


        event.setTargetStartDate(
                LocalDate.now().minusDays(10));


        when(shiftApplicationEventRepository.findAll())
                .thenReturn(
                        List.of(event));



        ShiftApplicationEvent result =
                service.getOldestEvent();



        assertNotNull(result);


        assertEquals(
                1,
                result.getEventId());

    }





    @Test
    @DisplayName("イベント一覧にnullが含まれる場合は例外")
    void getOldestEventContainsNull() {


        ShiftApplicationEvent event =
                createEvent();


        List<ShiftApplicationEvent> list =
                new java.util.ArrayList<>();


        list.add(null);
        list.add(event);



        when(shiftApplicationEventRepository.findAll())
                .thenReturn(list);



        assertThrows(
                NullPointerException.class,
                () -> service.getOldestEvent()
        );

    }





    





    @Test
    @DisplayName("存在しない大きなイベントID")
    void getCurrentEventLargeId() {


        when(shiftApplicationEventRepository.findById(999999))
                .thenReturn(
                        Optional.empty());



        ShiftApplicationEvent result =
                service.getCurrentEvent(999999);



        assertNull(result);

    }





    @Test
    @DisplayName("保存後Repository呼び出し1回")
    void saveShiftOnce() {


        Shift shift =
                createShift();



        service.saveShift(shift);



        verify(
                shiftRepository,
                times(1))
                .save(shift);

    }





    @Test
    @DisplayName("保存対象ShiftのeventId null")
    void saveShiftNullEventId() {


        Shift shift =
                createShift();


        shift.setEventId(null);



        service.saveShift(shift);



        verify(
                shiftRepository)
                .save(shift);

    }





    @Test
    @DisplayName("保存対象ShiftのuserId null")
    void saveShiftNullUserId() {


        Shift shift =
                createShift();


        shift.setUserId(null);



        service.saveShift(shift);



        verify(
                shiftRepository)
                .save(shift);

    }





    @Test
    @DisplayName("1ユーザー複数勤務日集計")
    void monthlySummaryMultipleDays() {


        Shift shift1 =
                createShift();


        Shift shift2 =
                createShift();



        shift2.setShiftDate(
                LocalDate.of(2026,8,2));



        Map<String,List<MonthlyShiftSummaryDto>> result =
                service.getMonthlySummaryMap(
                        List.of(
                                shift1,
                                shift2),
                        List.of(createUser()));



        assertEquals(
                2,
                result.get("user001")
                .get(0)
                .getWorkingDays());

    }





    @Test
    @DisplayName("複数ユーザー同月集計")
    void monthlySummarySameMonthMultipleUsers() {


        Users user2 =
                new Users();


        user2.setUserId(
                "user002");



        Shift shift2 =
                createShift();


        shift2.setUserId(
                "user002");



        Map<String,List<MonthlyShiftSummaryDto>> result =
                service.getMonthlySummaryMap(
                        List.of(
                                createShift(),
                                shift2),
                        List.of(
                                createUser(),
                                user2));



        assertEquals(
                2,
                result.size());

    }





    @Test
    @DisplayName("勤務不可ユーザー集計")
    void monthlySummaryUnavailableUser() {


        Shift shift =
                createShift();


        shift.setIsAvailable(0);



        Map<String,List<MonthlyShiftSummaryDto>> result =
                service.getMonthlySummaryMap(
                        List.of(shift),
                        List.of(createUser()));



        assertTrue(
                result.get("user001")
                .isEmpty());

    }





    @Test
    @DisplayName("シフト日付未来の場合")
    void monthlySummaryFutureDate() {


        Shift shift =
                createShift();



        shift.setShiftDate(
                LocalDate.of(2030,1,1));



        Map<String,List<MonthlyShiftSummaryDto>> result =
                service.getMonthlySummaryMap(
                        List.of(shift),
                        List.of(createUser()));



        assertNotNull(
                result.get("user001"));

    }





    @Test
    @DisplayName("Repository保存時RuntimeException")
    void saveShiftRuntimeException() {


        Shift shift =
                createShift();



        doThrow(
                new RuntimeException("error")
        )
        .when(shiftRepository)
        .save(shift);



        assertThrows(
                RuntimeException.class,
                () -> service.saveShift(shift));

    }





    @Test
    @DisplayName("イベント取得Repository呼出確認")
    void getOldestEventVerifyRepository() {


        when(shiftApplicationEventRepository.findAll())
                .thenReturn(
                        List.of(createEvent()));



        service.getOldestEvent();



        verify(
                shiftApplicationEventRepository,
                times(1))
                .findAll();

    }

}