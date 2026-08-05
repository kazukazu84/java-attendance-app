package com.example.adminshift.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataRetrievalFailureException;

import com.example.adminshift.dto.MonthlyShiftSummaryDto;
import com.example.adminshift.entity.Shift;
import com.example.adminshift.entity.ShiftApplicationEvent;
import com.example.adminshift.entity.Users;
import com.example.adminshift.repository.ShiftApplicationEventRepository;
import com.example.adminshift.repository.ShiftRepository;
import com.example.adminshift.repository.UsersRepository;
import com.example.attendance.entity.Attendance;
import com.example.attendance.repository.AttendanceRepository;

@ExtendWith(MockitoExtension.class)
class ShiftCreateServiceTest {

    @Mock
    private ShiftApplicationEventRepository shiftApplicationEventRepository;

    @Mock
    private ShiftRepository shiftRepository;

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private AttendanceRepository attendanceRepository;

    @InjectMocks
    private ShiftCreateService shiftCreateService;

    @Nested
    @DisplayName("getEventList のテスト")
    class GetEventListTest {

        @Test
        @DisplayName("正常系：イベントリストを正常に取得できること")
        void getEventList_Success() {
            List<ShiftApplicationEvent> expected = List.of(new ShiftApplicationEvent());
            when(shiftApplicationEventRepository.findAll()).thenReturn(expected);

            List<ShiftApplicationEvent> actual = shiftCreateService.getEventList();

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("異常系：例外が発生した場合は空リストを返すこと")
        void getEventList_DataAccessException() {
            when(shiftApplicationEventRepository.findAll())
                    .thenThrow(new DataRetrievalFailureException("DB error"));

            List<ShiftApplicationEvent> actual = shiftCreateService.getEventList();

            assertThat(actual).isEmpty();
        }
    }

    @Nested
    @DisplayName("getOldestEvent のテスト")
    class GetOldestEventTest {

        @Test
        @DisplayName("優先度①：明日以降に対象期間が始まる未来イベントが返されること")
        void getOldestEvent_Priority1_FutureEvent() {
            LocalDate today = LocalDate.now();

            ShiftApplicationEvent futureEvent = new ShiftApplicationEvent();
            futureEvent.setTargetStartDate(today.plusDays(1));

            when(shiftApplicationEventRepository.findAll()).thenReturn(List.of(futureEvent));

            ShiftApplicationEvent actual = shiftCreateService.getOldestEvent();

            assertThat(actual).isEqualTo(futureEvent);
        }

        @Test
        @DisplayName("優先度②：未来イベントが無く、受付期間内のイベントが返されること")
        void getOldestEvent_Priority2_InApplicationPeriod() {
            LocalDate today = LocalDate.now();

            ShiftApplicationEvent currentEvent = new ShiftApplicationEvent();
            currentEvent.setTargetStartDate(today.minusDays(5)); // 未来ではない
            currentEvent.setApplicationStartDate(today.minusDays(2));
            currentEvent.setApplicationEndDate(today.plusDays(2));

            when(shiftApplicationEventRepository.findAll()).thenReturn(List.of(currentEvent));

            ShiftApplicationEvent actual = shiftCreateService.getOldestEvent();

            assertThat(actual).isEqualTo(currentEvent);
        }

        @Test
        @DisplayName("優先度③：条件に合致しない場合、リストの最後のイベントが返されること")
        void getOldestEvent_Priority3_FallbackLastEvent() {
            LocalDate today = LocalDate.now();

            ShiftApplicationEvent oldEvent1 = new ShiftApplicationEvent();
            oldEvent1.setTargetStartDate(today.minusDays(20));

            ShiftApplicationEvent oldEvent2 = new ShiftApplicationEvent();
            oldEvent2.setTargetStartDate(today.minusDays(10));

            when(shiftApplicationEventRepository.findAll()).thenReturn(List.of(oldEvent1, oldEvent2));

            ShiftApplicationEvent actual = shiftCreateService.getOldestEvent();

            assertThat(actual).isEqualTo(oldEvent2);
        }

        @Test
        @DisplayName("異常系：リストが空の場合はnullを返すこと")
        void getOldestEvent_EmptyList() {
            when(shiftApplicationEventRepository.findAll()).thenReturn(Collections.emptyList());

            ShiftApplicationEvent actual = shiftCreateService.getOldestEvent();

            assertThat(actual).isNull();
        }
    }

    @Nested
    @DisplayName("getCurrentEvent のテスト")
    class GetCurrentEventTest {

        @Test
        @DisplayName("正常系：ID指定でイベントを取得できること")
        void getCurrentEvent_Success() {
            ShiftApplicationEvent event = new ShiftApplicationEvent();
            when(shiftApplicationEventRepository.findById(1)).thenReturn(Optional.of(event));

            ShiftApplicationEvent actual = shiftCreateService.getCurrentEvent(1);

            assertThat(actual).isEqualTo(event);
        }

        @Test
        @DisplayName("引数がnullの場合はnullを返しリポジトリを呼び出さないこと")
        void getCurrentEvent_NullId() {
            ShiftApplicationEvent actual = shiftCreateService.getCurrentEvent(null);

            assertThat(actual).isNull();
            verify(shiftApplicationEventRepository, never()).findById(any());
        }
    }

    @Nested
    @DisplayName("getTargetDateList のテスト")
    class GetTargetDateListTest {

//        @Test
//        @DisplayName("正常系：開始日から終了日までの日付リストが生成されること")
//        void getTargetDateList_Success() {
//            ShiftApplicationEvent event = new ShiftApplicationEvent();
//            event.setStartDate(LocalDate.of(2026, 8, 1));
//            event.setEndDate(LocalDate.of(2026, 8, 3));
//
//            List<LocalDate> actual = shiftCreateService.getTargetDateList(event);
//
//            assertThat(actual).containsExactly(
//                    LocalDate.of(2026, 8, 1),
//                    LocalDate.of(2026, 8, 2),
//                    LocalDate.of(2026, 8, 3)
//            );
//        }

        @Test
        @DisplayName("異常系：eventや日付がnullの場合は空リストを返すこと")
        void getTargetDateList_Null() {
            List<LocalDate> actual = shiftCreateService.getTargetDateList(null);

            assertThat(actual).isEmpty();
        }
    }

    @Nested
    @DisplayName("saveShift のテスト")
    class SaveShiftTest {

        @Test
        @DisplayName("正常系：saveが正しく呼び出されること")
        void saveShift_Success() {
            Shift shift = new Shift();

            shiftCreateService.saveShift(shift);

            verify(shiftRepository).save(shift);
        }

        @Test
        @DisplayName("引数がnullの場合はsaveを呼び出さないこと")
        void saveShift_Null() {
            shiftCreateService.saveShift(null);

            verify(shiftRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("getMonthlySummaryMap のテスト")
    class GetMonthlySummaryMapTest {

        @Test
        @DisplayName("正常系：過去日（勤怠実績）と未来日（シフト予定・夜勤含）が集計されること")
        void getMonthlySummaryMap_Success() {
            Integer eventId = 100;
            Users user = new Users();
            user.setUserId("user01");

            LocalDate today = LocalDate.now();
            LocalDate pastDate = today.minusDays(1);
            LocalDate futureDate = today.plusDays(1);

            ShiftApplicationEvent event = new ShiftApplicationEvent();
            event.setTargetStartDate(pastDate);
            event.setTargetEndDate(futureDate);

            when(shiftApplicationEventRepository.findById(eventId)).thenReturn(Optional.of(event));

            // 過去日：勤怠データ (09:00〜18:00 休憩1.0時間 = 8時間 / 480分)
            Attendance attendance = new Attendance();
            attendance.setClockIn(LocalTime.of(9, 0));
            attendance.setClockOut(LocalTime.of(18, 0));
            attendance.setRestTime(1.0);
            when(attendanceRepository.findByUserIdAndWorkDate("user01", pastDate))
                    .thenReturn(Optional.of(attendance));

            // 未来日：シフトデータ (22:00〜05:00 夜勤 = 7時間 / 420分)
            Shift shift = new Shift();
            shift.setUserId("user01");
            shift.setShiftDate(futureDate);
            shift.setIsAvailable(1);
            shift.setStartTime(LocalTime.of(22, 0));
            shift.setEndTime(LocalTime.of(5, 0));

            when(shiftRepository.findByShiftDateBetween(any(LocalDate.class), any(LocalDate.class)))
                    .thenReturn(List.of(shift));

            // 実行
            Map<String, List<MonthlyShiftSummaryDto>> result =
                    shiftCreateService.getMonthlySummaryMap(eventId, List.of(user));

            // 検証
            assertThat(result).containsKey("user01");
            List<MonthlyShiftSummaryDto> summaries = result.get("user01");
            assertThat(summaries).hasSize(1);

            MonthlyShiftSummaryDto summary = summaries.get(0);
            assertThat(summary.getWorkingDays()).isEqualTo(2);
            assertThat(summary.getTotalMinutes()).isEqualTo(900); // 480分 + 420分
        }

        @Test
        @DisplayName("異常系：パラメータが不正な場合は空のMapを返すこと")
        void getMonthlySummaryMap_InvalidParams() {
            Map<String, List<MonthlyShiftSummaryDto>> result =
                    shiftCreateService.getMonthlySummaryMap(null, Collections.emptyList());

            assertThat(result).isEmpty();
        }
    }
}