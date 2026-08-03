package com.example.adminshift.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
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



    private ShiftApplicationEvent createEvent(){

        ShiftApplicationEvent event =
                new ShiftApplicationEvent();

        event.setEventId(1);

        event.setTargetStartDate(
                LocalDate.of(2026,8,1));

        event.setTargetEndDate(
                LocalDate.of(2026,8,3));

        event.setApplicationStartDate(
                LocalDate.of(2026,7,1));

        event.setApplicationEndDate(
                LocalDate.of(2026,7,20));

        return event;
    }



    private Users createUser() {

        Users user =
                new Users();

        user.setUserId("user001");

        return user;
    }



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
    @DisplayName("イベント取得時DBエラー")
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
    @DisplayName("イベントID null")
    void getCurrentEventNull() {


        assertNull(
                service.getCurrentEvent(null));


        verify(
                shiftApplicationEventRepository,
                never())
                .findById(any());

    }



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
    @DisplayName("対象日付一覧生成")
    void getTargetDateList() {


        List<LocalDate> result =
                service.getTargetDateList(createEvent());


        assertEquals(
                3,
                result.size());


        assertEquals(
                LocalDate.of(2026,8,1),
                result.get(0));

    }



    @Test
    @DisplayName("イベントnullなら空")
    void getTargetDateListNull() {


        List<LocalDate> result =
                service.getTargetDateList(null);


        assertTrue(
                result.isEmpty());

    }



    @Test
    @DisplayName("ユーザー一覧取得")
    void getAllUsers() {


        when(usersRepository.findAll())
                .thenReturn(
                        List.of(createUser()));


        List<Users> result =
                service.getAllUsers();


        assertEquals(
                1,
                result.size());

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
    @DisplayName("シフト保存")
    void saveShift() {


        Shift shift =
                createShift();


        service.saveShift(shift);


        verify(shiftRepository)
                .save(shift);

    }



    @Test
    @DisplayName("null保存")
    void saveShiftNull() {


        service.saveShift(null);


        verify(
                shiftRepository,
                never())
                .save(any());

    }



    @Test
    @DisplayName("月間勤務集計")
    void getMonthlySummaryMap() {


        Map<String,List<MonthlyShiftSummaryDto>> result =
                service.getMonthlySummaryMap(
                        List.of(createShift()),
                        List.of(createUser()));


        List<MonthlyShiftSummaryDto> summary =
                result.get("user001");


        assertNotNull(summary);


        assertEquals(
                YearMonth.of(2026,8),
                summary.get(0).getYearMonth());


        assertEquals(
                1,
                summary.get(0).getWorkingDays());


        assertEquals(
                540,
                summary.get(0).getTotalMinutes());

    }



    @Test
    @DisplayName("夜勤集計")
    void getMonthlySummaryNightShift() {


        Shift shift =
                createShift();


        shift.setStartTime(
                LocalTime.of(22,0));


        shift.setEndTime(
                LocalTime.of(6,0));


        Map<String,List<MonthlyShiftSummaryDto>> result =
                service.getMonthlySummaryMap(
                        List.of(shift),
                        List.of(createUser()));


        assertEquals(
                480,
                result.get("user001")
                .get(0)
                .getTotalMinutes());

    }
    
    @Test
    @DisplayName("イベント一覧Repositoryがnullを返す場合")
    void getEventListNull() {

        when(shiftApplicationEventRepository.findAll())
                .thenReturn(null);


        List<ShiftApplicationEvent> result =
                service.getEventList();


        assertTrue(result.isEmpty());

    }
    @Test
    @DisplayName("イベントなしの場合")
    void getOldestEventEmpty() {

        when(shiftApplicationEventRepository.findAll())
                .thenReturn(List.of());


        ShiftApplicationEvent result =
                service.getOldestEvent();


        assertNull(result);

    }
    @Test
    @DisplayName("カレントイベントが存在しない場合")
    void getCurrentEventNotFound() {

        when(shiftApplicationEventRepository.findById(99))
                .thenReturn(Optional.empty());


        ShiftApplicationEvent result =
                service.getCurrentEvent(99);


        assertNull(result);

    }
    @Test
    @DisplayName("シフト一覧eventId null")
    void getShiftTableNull() {


        List<Shift> result =
                service.getShiftTable(null);


        assertTrue(result.isEmpty());


        verify(
            shiftRepository,
            never())
            .findByEventId(any());

    }
    @Test
    @DisplayName("ユーザー一覧Repository null")
    void getAllUsersNull() {

        when(usersRepository.findAll())
                .thenReturn(null);


        List<Users> result =
                service.getAllUsers();


        assertTrue(result.isEmpty());

    }
    @Test
    @DisplayName("シフト詳細なし")
    void getShiftDetailNotFound() {


        when(shiftRepository.findById(1))
                .thenReturn(Optional.empty());


        Shift result =
                service.getShiftDetail(1);


        assertNull(result);

    }
    @Test
    @DisplayName("isAvailableが0の場合集計対象外")
    void monthlySummaryUnavailableShift() {


        Shift shift =
                createShift();


        shift.setIsAvailable(0);


        Map<String,List<MonthlyShiftSummaryDto>> result =
                service.getMonthlySummaryMap(
                        List.of(shift),
                        List.of(createUser()));


        assertTrue(
                result.get("user001").isEmpty());

    }
    @Test
    @DisplayName("シフトリストnullの場合")
    void monthlySummaryNullShiftList() {


        Map<String,List<MonthlyShiftSummaryDto>> result =
                service.getMonthlySummaryMap(
                        null,
                        List.of(createUser()));


        assertNotNull(
                result.get("user001"));


        assertTrue(
                result.get("user001").isEmpty());

    }
    @Test
    @DisplayName("ユーザーリストなしの場合")
    void monthlySummaryNoUsers() {


        Map<String,List<MonthlyShiftSummaryDto>> result =
                service.getMonthlySummaryMap(
                        List.of(createShift()),
                        List.of());


        assertTrue(result.isEmpty());

    }
    @Test
    @DisplayName("日跨ぎ勤務集計")
    void monthlySummaryOverNight() {


        Shift shift =
                createShift();


        shift.setStartTime(
                LocalTime.of(23,0));


        shift.setEndTime(
                LocalTime.of(7,0));


        Map<String,List<MonthlyShiftSummaryDto>> result =
                service.getMonthlySummaryMap(
                        List.of(shift),
                        List.of(createUser()));


        assertEquals(
                480,
                result.get("user001")
                .get(0)
                .getTotalMinutes());

    }
    
    @Test
    @DisplayName("受付期間中イベント取得")
    void getOldestEventApplicationPeriod() {


        ShiftApplicationEvent event =
                createEvent();


        event.setApplicationStartDate(
                LocalDate.now().minusDays(1));


        event.setApplicationEndDate(
                LocalDate.now().plusDays(1));


        // 未来イベント判定を外す
        event.setTargetStartDate(
                LocalDate.now().minusDays(5));


        when(shiftApplicationEventRepository.findAll())
                .thenReturn(List.of(event));


        ShiftApplicationEvent result =
                service.getOldestEvent();


        assertNotNull(result);

        assertEquals(
                1,
                result.getEventId());

    }
    @Test
    @DisplayName("未来イベントは最初のイベントを取得")
    void getOldestEventMultipleFuture() {


        ShiftApplicationEvent event1 =
                createEvent();


        ShiftApplicationEvent event2 =
                createEvent();


        event2.setEventId(2);


        when(shiftApplicationEventRepository.findAll())
                .thenReturn(
                        List.of(event1,event2));


        ShiftApplicationEvent result =
                service.getOldestEvent();


        assertEquals(
                1,
                result.getEventId());

    }
    @Test
    @DisplayName("開始日と終了日が同日の場合")
    void getTargetDateListSameDay() {


        ShiftApplicationEvent event =
                createEvent();


        event.setTargetEndDate(
                event.getTargetStartDate());


        List<LocalDate> result =
                service.getTargetDateList(event);


        assertEquals(
                1,
                result.size());

    }
    @Test
    @DisplayName("終了日が開始日前の場合")
    void getTargetDateListInvalidRange() {


        ShiftApplicationEvent event =
                createEvent();


        event.setTargetStartDate(
                LocalDate.of(2026,8,10));


        event.setTargetEndDate(
                LocalDate.of(2026,8,1));


        List<LocalDate> result =
                service.getTargetDateList(event);


        assertTrue(
                result.isEmpty());

    }
    @Test
    @DisplayName("複数ユーザー月間集計")
    void monthlySummaryMultipleUsers() {


        Users user2 =
                new Users();

        user2.setUserId("user002");


        Shift shift2 =
                createShift();

        shift2.setUserId("user002");


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


        assertNotNull(
                result.get("user001"));


        assertNotNull(
                result.get("user002"));

    }
    @Test
    @DisplayName("複数月勤務集計")
    void monthlySummaryMultipleMonth() {


        Shift august =
                createShift();


        Shift september =
                createShift();


        september.setShiftDate(
                LocalDate.of(2026,9,1));


        Map<String,List<MonthlyShiftSummaryDto>> result =
                service.getMonthlySummaryMap(
                        List.of(
                                august,
                                september),
                        List.of(createUser()));


        assertEquals(
                2,
                result.get("user001").size());

    }
    @Test
    @DisplayName("時間未設定シフト除外")
    void monthlySummaryIncompleteShift() {


        Shift shift =
                createShift();


        shift.setStartTime(null);


        Map<String,List<MonthlyShiftSummaryDto>> result =
                service.getMonthlySummaryMap(
                        List.of(shift),
                        List.of(createUser()));


        assertTrue(
                result.get("user001").isEmpty());

    }
    @Test
    @DisplayName("nullシフト除外")
    void monthlySummaryNullShift() {


        Map<String,List<MonthlyShiftSummaryDto>> result =
                service.getMonthlySummaryMap(
                        List.of(
                                null,
                                createShift()),
                        List.of(createUser()));


        assertEquals(
                1,
                result.get("user001")
                .get(0)
                .getWorkingDays());

    }
    @Test
    @DisplayName("nullユーザー除外")
    void monthlySummaryNullUser() {


        Map<String,List<MonthlyShiftSummaryDto>> result =
                service.getMonthlySummaryMap(
                        List.of(createShift()),
                        List.of(
                                null,
                                createUser()));


        assertNotNull(
                result.get("user001"));

    }
    @Test
    @DisplayName("保存時DBエラー")
    void saveShiftException() {


        Shift shift =
                createShift();


        doThrow(
                new DataAccessException("error"){}
        )
        .when(shiftRepository)
        .save(shift);


        assertThrows(
                DataAccessException.class,
                () -> service.saveShift(shift));

    }

}