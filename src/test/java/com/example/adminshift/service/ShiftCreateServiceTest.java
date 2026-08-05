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
        @DisplayName("正常系：イベントリストを取得できること")
        void getEventList_Success() {
            List<ShiftApplicationEvent> expected = List.of(new ShiftApplicationEvent());
            when(shiftApplicationEventRepository.findAll()).thenReturn(expected);

            List<ShiftApplicationEvent> actual = shiftCreateService.getEventList();

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("異常系：取得結果がnullの場合は空リストを返すこと")
        void getEventList_NullResult() {
            when(shiftApplicationEventRepository.findAll()).thenReturn(null);

            List<ShiftApplicationEvent> actual = shiftCreateService.getEventList();

            assertThat(actual).isEmpty();
        }
    }

    @Nested
    @DisplayName("getOldestEvent のテスト")
    class GetOldestEventTest {

        @Test
        @DisplayName("正常系：現在受付期間内で最も対象開始日が早いイベントを取得できること")
        void getOldestEvent_Success() {
            LocalDate today = LocalDate.now();

            // イベント1：受付期間外（終了済）
            ShiftApplicationEvent event1 = new ShiftApplicationEvent();
            event1.setApplicationStartDate(today.minusDays(10));
            event1.setApplicationEndDate(today.minusDays(5));
            event1.setTargetStartDate(today.plusDays(10));

            // イベント2：受付期間内（対象開始日：遅め）
            ShiftApplicationEvent event2 = new ShiftApplicationEvent();
            event2.setApplicationStartDate(today.minusDays(1));
            event2.setApplicationEndDate(today.plusDays(1));
            event2.setTargetStartDate(today.plusDays(20));

            // イベント3：受付期間内（対象開始日：早め -> これが選ばれるべき）
            ShiftApplicationEvent event3 = new ShiftApplicationEvent();
            event3.setApplicationStartDate(today.minusDays(2));
            event3.setApplicationEndDate(today.plusDays(2));
            event3.setTargetStartDate(today.plusDays(15));

            when(shiftApplicationEventRepository.findAll())
                    .thenReturn(List.of(event1, event2, event3));

            ShiftApplicationEvent actual = shiftCreateService.getOldestEvent();

            assertThat(actual).isNotNull();
            assertThat(actual).isEqualTo(event3);
        }

        @Test
        @DisplayName("異常系：該当するイベントがない場合はnullを返すこと")
        void getOldestEvent_NotFound() {
            when(shiftApplicationEventRepository.findAll()).thenReturn(Collections.emptyList());

            ShiftApplicationEvent actual = shiftCreateService.getOldestEvent();

            assertThat(actual).isNull();
        }
    }

    @Nested
    @DisplayName("getCurrentEvent のテスト")
    class GetCurrentEventTest {

        @Test
        @DisplayName("正常系：IDを指定してイベントを取得できること")
        void getCurrentEvent_Success() {
            ShiftApplicationEvent event = new ShiftApplicationEvent();
            when(shiftApplicationEventRepository.findById(1)).thenReturn(Optional.of(event));

            ShiftApplicationEvent actual = shiftCreateService.getCurrentEvent(1);

            assertThat(actual).isEqualTo(event);
        }

        @Test
        @DisplayName("引数がnullの場合はnullを返すこと")
        void getCurrentEvent_NullId() {
            ShiftApplicationEvent actual = shiftCreateService.getCurrentEvent(null);

            assertThat(actual).isNull();
            verify(shiftApplicationEventRepository, never()).findById(any());
        }
    }

    @Nested
    @DisplayName("getShiftTable のテスト")
    class GetShiftTableTest {

        @Test
        @DisplayName("正常系：イベントIDに対応するシフト一覧を取得できること")
        void getShiftTable_Success() {
            List<Shift> expected = List.of(new Shift());
            when(shiftRepository.findByEventId(1)).thenReturn(expected);

            List<Shift> actual = shiftCreateService.getShiftTable(1);

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("引数がnullの場合は空リストを返すこと")
        void getShiftTable_NullId() {
            List<Shift> actual = shiftCreateService.getShiftTable(null);

            assertThat(actual).isEmpty();
            verify(shiftRepository, never()).findByEventId(any());
        }
    }

    @Nested
    @DisplayName("getTargetDateList のテスト")
    class GetTargetDateListTest {

        @Test
        @DisplayName("正常系：開始日から終了日までの日付リストが正しく生成されること")
        void getTargetDateList_Success() {
            ShiftApplicationEvent event = new ShiftApplicationEvent();
            event.setStartDate(LocalDate.of(2026, 8, 1));
            event.setEndDate(LocalDate.of(2026, 8, 3));

            List<LocalDate> actual = shiftCreateService.getTargetDateList(event);

            assertThat(actual).containsExactly(
                    LocalDate.of(2026, 8, 1),
                    LocalDate.of(2026, 8, 2),
                    LocalDate.of(2026, 8, 3)
            );
        }

        @Test
        @DisplayName("異常系：eventや日付がnullの場合は空リストを返すこと")
        void getTargetDateList_NullEvent() {
            List<LocalDate> actual = shiftCreateService.getTargetDateList(null);

            assertThat(actual).isEmpty();
        }
    }

    @Nested
    @DisplayName("getAllUsers のテスト")
    class GetAllUsersTest {

        @Test
        @DisplayName("正常系：ユーザー一覧を取得できること")
        void getAllUsers_Success() {
            List<Users> expected = List.of(new Users());
            when(usersRepository.findAll()).thenReturn(expected);

            List<Users> actual = shiftCreateService.getAllUsers();

            assertThat(actual).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("getShiftDetail のテスト")
    class GetShiftDetailTest {

        @Test
        @DisplayName("正常系：シフト詳細を取得できること")
        void getShiftDetail_Success() {
            Shift shift = new Shift();
            when(shiftRepository.findById(10)).thenReturn(Optional.of(shift));

            Shift actual = shiftCreateService.getShiftDetail(10);

            assertThat(actual).isEqualTo(shift);
        }

        @Test
        @DisplayName("引数がnullの場合はnullを返すこと")
        void getShiftDetail_NullId() {
            Shift actual = shiftCreateService.getShiftDetail(null);

            assertThat(actual).isNull();
        }
    }

    @Nested
    @DisplayName("saveShift のテスト")
    class SaveShiftTest {

        @Test
        @DisplayName("正常系：シフトが保存されること")
        void saveShift_Success() {
            Shift shift = new Shift();

            shiftCreateService.saveShift(shift);

            verify(shiftRepository).save(shift);
        }

        @Test
        @DisplayName("引数がnullの場合は保存処理が行われないこと")
        void saveShift_Null() {
            shiftCreateService.saveShift(null);

            verify(shiftRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("getMonthlySummaryMap のテスト")
    class GetMonthlySummaryMapTest {

        @Test
        @DisplayName("正常系：過去日（勤怠実績）と未来日（シフト予定）を正しく計算して月間集計を返せること")
        void getMonthlySummaryMap_Success() {
            // モック準備
            Integer eventId = 1;

            Users user = new Users();
            user.setUserId("user01");
            List<Users> userList = List.of(user);

            LocalDate today = LocalDate.now();
            LocalDate pastDate = today.minusDays(1); // 過去日
            LocalDate futureDate = today.plusDays(1); // 未来日

            // イベント期間の設定
            ShiftApplicationEvent event = new ShiftApplicationEvent();
            event.setTargetStartDate(pastDate);
            event.setTargetEndDate(futureDate);
            when(shiftApplicationEventRepository.findById(eventId)).thenReturn(Optional.of(event));

            // 過去日：勤怠データ (8時間労働, 休憩1.0時間 = 7時間 / 420分)
            Attendance attendance = new Attendance();
            attendance.setClockIn(LocalTime.of(9, 0));
            attendance.setClockOut(LocalTime.of(17, 0));
            attendance.setRestTime(1.0);
            when(attendanceRepository.findByUserIdAndWorkDate("user01", pastDate))
                    .thenReturn(Optional.of(attendance));

            // 未来日：シフトデータ (22:00〜翌5:00の夜勤 = 7時間 / 420分)
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
                    shiftCreateService.getMonthlySummaryMap(eventId, userList);

            // 検証
            assertThat(result).containsKey("user01");
            List<MonthlyShiftSummaryDto> summaryList = result.get("user01");
            assertThat(summaryList).hasSize(1);

            MonthlyShiftSummaryDto summary = summaryList.get(0);
            assertThat(summary.getWorkingDays()).isEqualTo(2); // 過去1日 + 未来1日 = 2日
            assertThat(summary.getTotalMinutes()).isEqualTo(840); // 420分 + 420分 = 840分
        }

        @Test
        @DisplayName("引数が不適切な場合は空のMapを返すこと")
        void getMonthlySummaryMap_InvalidInputs() {
            Map<String, List<MonthlyShiftSummaryDto>> result =
                    shiftCreateService.getMonthlySummaryMap(null, Collections.emptyList());

            assertThat(result).isEmpty();
        }
    }
}