package com.example.adminshift.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.adminshift.dto.MonthlyShiftSummaryDto;
import com.example.adminshift.entity.Shift;
import com.example.adminshift.entity.ShiftApplicationEvent;
import com.example.adminshift.entity.Users;
import com.example.adminshift.repository.ShiftApplicationEventRepository;
import com.example.adminshift.repository.ShiftRepository;
import com.example.adminshift.repository.UsersRepository;
import com.example.attendance.entity.Attendance;
import com.example.attendance.repository.AttendanceRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShiftCreateService {

    private final ShiftApplicationEventRepository shiftApplicationEventRepository;
    private final AttendanceRepository attendanceRepository;
    private final ShiftRepository shiftRepository;
    private final UsersRepository usersRepository;

    /**
     * シフト詳細情報（1件）を取得します。
     */
    public Shift getShiftDetail(Integer shiftId) {
        if (shiftId == null) {
            return null;
        }
        return shiftRepository.findById(shiftId).orElse(null);
    }

    /**
     * シフト情報を保存・更新します。
     */
    public void saveShift(Shift shift) {
        if (shift != null) {
            shiftRepository.save(shift);
        }
    }

    /**
     * 全イベントリストを取得します。
     */
    public List<ShiftApplicationEvent> getEventList() {
        return shiftApplicationEventRepository.findAll();
    }

    /**
     * Controller互換用: 初期表示対象のデフォルトイベントを取得します。
     */
    public ShiftApplicationEvent getOldestEvent() {
        return getDefaultDisplayEvent();
    }

    /**
     * イベントIDからイベント情報を取得します。
     */
    public ShiftApplicationEvent getCurrentEvent(Integer eventId) {
        if (eventId == null) {
            return getDefaultDisplayEvent();
        }
        return shiftApplicationEventRepository.findById(eventId).orElse(null);
    }

    /**
     * 全ユーザーリストを取得します。
     */
    public List<Users> getAllUsers() {
        return usersRepository.findAll();
    }

    /**
     * 指定イベントのシフト一覧を取得します。
     * （Controllerでシフトテーブル表示用に使用）
     */
    public List<Shift> getShiftTable(Integer eventId) {
        if (eventId == null) {
            return Collections.emptyList();
        }
        return shiftRepository.findByEventId(eventId);
    }

    /**
     * 表示対象となるイベントを取得します。
     * 1. 本日が含まれる進行中イベント
     * 2. なければ未来のイベント（開始日が最も近いもの）
     * 3. なければ全イベントの中で最も新しいイベント
     */
    public ShiftApplicationEvent getDefaultDisplayEvent() {
        List<ShiftApplicationEvent> eventList = shiftApplicationEventRepository.findAll();
        if (eventList == null || eventList.isEmpty()) {
            return null;
        }

        LocalDate today = LocalDate.now();

        // ① 本日が含まれる進行中イベント
        ShiftApplicationEvent currentEvent = eventList.stream()
                .filter(e -> e.getTargetStartDate() != null && e.getTargetEndDate() != null)
                .filter(e -> !today.isBefore(e.getTargetStartDate()) && !today.isAfter(e.getTargetEndDate()))
                .findFirst()
                .orElse(null);

        if (currentEvent != null) {
            return currentEvent;
        }

        // ② 本日より未来のイベント（開始日が最も近いもの）
        ShiftApplicationEvent futureEvent = eventList.stream()
                .filter(e -> e.getTargetStartDate() != null)
                .filter(e -> e.getTargetStartDate().isAfter(today))
                .min(Comparator.comparing(ShiftApplicationEvent::getTargetStartDate))
                .orElse(null);

        if (futureEvent != null) {
            return futureEvent;
        }

        // ③ 該当がなければ、全イベントの中で最新のもの
        return eventList.stream()
                .filter(e -> e.getTargetStartDate() != null)
                .max(Comparator.comparing(ShiftApplicationEvent::getTargetStartDate))
                .orElse(eventList.get(eventList.size() - 1));
    }

    /**
     * イベント期間内の日付リストを取得します。
     */
    public List<LocalDate> getTargetDateList(ShiftApplicationEvent event) {
        if (event == null || event.getTargetStartDate() == null || event.getTargetEndDate() == null) {
            return Collections.emptyList();
        }

        List<LocalDate> dateList = new ArrayList<>();
        LocalDate current = event.getTargetStartDate();
        LocalDate end = event.getTargetEndDate();

        while (!current.isAfter(end)) {
            dateList.add(current);
            current = current.plusDays(1);
        }
        return dateList;
    }

    /**
     * ユーザーごとの月別勤務サマリーマップを取得します。
     */
    public Map<String, List<MonthlyShiftSummaryDto>> getMonthlySummaryMap(
            Integer eventId, List<Users> userList) {

        Map<String, List<MonthlyShiftSummaryDto>> summaryMap = new LinkedHashMap<>();

        if (eventId == null || userList == null || userList.isEmpty()) {
            return summaryMap;
        }

        ShiftApplicationEvent event = shiftApplicationEventRepository.findById(eventId).orElse(null);
        if (event == null || event.getTargetStartDate() == null || event.getTargetEndDate() == null) {
            return summaryMap;
        }

        LocalDate eventStart = event.getTargetStartDate();
        LocalDate eventEnd = event.getTargetEndDate();
        LocalDate today = LocalDate.now();

        List<String> userIds = userList.stream()
                .map(Users::getUserId)
                .filter(Objects::nonNull)
                .toList();

        if (userIds.isEmpty()) {
            return summaryMap;
        }

        List<Attendance> allAttendances = attendanceRepository.findByUserIdInAndWorkDateBetween(userIds, eventStart, eventEnd);
        Map<String, Map<LocalDate, Attendance>> attendanceMap = allAttendances.stream()
                .collect(Collectors.groupingBy(
                        Attendance::getUserId,
                        Collectors.toMap(Attendance::getWorkDate, a -> a, (k1, k2) -> k1)
                ));

        List<Shift> allShifts = shiftRepository.findByUserIdInAndShiftDateBetween(userIds, eventStart, eventEnd);
        Map<String, Map<LocalDate, Shift>> shiftMap = allShifts.stream()
                .collect(Collectors.groupingBy(
                        Shift::getUserId,
                        Collectors.toMap(Shift::getShiftDate, s -> s, (k1, k2) -> k1)
                ));

        for (Users user : userList) {
            String userId = user.getUserId();
            if (userId == null) continue;

            List<MonthlyShiftSummaryDto> monthlyList = new ArrayList<>();
            YearMonth month = YearMonth.from(eventStart);
            YearMonth endMonth = YearMonth.from(eventEnd);

            Map<LocalDate, Attendance> userAttendanceMap = attendanceMap.getOrDefault(userId, Collections.emptyMap());
            Map<LocalDate, Shift> userShiftMap = shiftMap.getOrDefault(userId, Collections.emptyMap());

            while (!month.isAfter(endMonth)) {
                LocalDate calcStart = month.atDay(1).isBefore(eventStart) ? eventStart : month.atDay(1);
                LocalDate calcEnd = month.atEndOfMonth().isAfter(eventEnd) ? eventEnd : month.atEndOfMonth();

                int workingDays = 0;
                long totalMinutes = 0;
                LocalDate date = calcStart;

                while (!date.isAfter(calcEnd)) {
                    boolean worked = false;
                    long minutes = 0;

                    if (!date.isAfter(today)) {
                        Attendance attendance = userAttendanceMap.get(date);
                        if (attendance != null && attendance.getClockIn() != null && attendance.getClockOut() != null) {
                            worked = true;
                            minutes = calculateAttendanceMinutes(attendance);
                        }
                    } else {
                        Shift shift = userShiftMap.get(date);
                        if (shift != null && Integer.valueOf(1).equals(shift.getIsAvailable())
                                && shift.getStartTime() != null && shift.getEndTime() != null) {
                            worked = true;
                            minutes = calculateShiftMinutes(shift);
                        }
                    }

                    if (worked) {
                        workingDays++;
                        totalMinutes += minutes;
                    }
                    date = date.plusDays(1);
                }

                if (workingDays > 0) {
                    monthlyList.add(new MonthlyShiftSummaryDto(month, workingDays, totalMinutes));
                }
                month = month.plusMonths(1);
            }

            if (!monthlyList.isEmpty()) {
                summaryMap.put(userId, monthlyList);
            }
        }

        return summaryMap;
    }

    private long calculateAttendanceMinutes(Attendance attendance) {
        try {
            LocalTime start = attendance.getClockIn();
            LocalTime end = attendance.getClockOut();

            if (start == null || end == null) return 0;

            long minutes = Duration.between(start, end).toMinutes();
            if (minutes < 0) {
                minutes += 24 * 60;
            }
            return Math.max(0, minutes);
        } catch (Exception e) {
            log.error("勤怠時間の計算中にエラーが発生しました: AttendanceId={}", attendance.getAttendanceId(), e);
            return 0;
        }
    }

    private long calculateShiftMinutes(Shift shift) {
        try {
            LocalTime start = shift.getStartTime();
            LocalTime end = shift.getEndTime();

            if (start == null || end == null) return 0;

            long minutes = Duration.between(start, end).toMinutes();
            if (minutes < 0) {
                minutes += 24 * 60;
            }
            return Math.max(0, minutes);
        } catch (Exception e) {
            log.error("シフト時間の計算中にエラーが発生しました: ShiftId={}", shift.getId(), e);
            return 0;
        }
    }
}