package com.example.adminshift.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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

/**
 * シフト作成画面に関する業務ロジックを提供するサービス実装クラス
 */
@Service
@RequiredArgsConstructor
public class ShiftCreateService {

    private final ShiftApplicationEventRepository shiftApplicationEventRepository;
    private final ShiftRepository shiftRepository;
    private final UsersRepository usersRepository;
    private final AttendanceRepository attendanceRepository;

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<ShiftApplicationEvent> getEventList() {
        try {
            List<ShiftApplicationEvent> list = shiftApplicationEventRepository.findAll();
            return (list != null) ? list : Collections.emptyList();
        } catch (DataAccessException e) {
            System.err.println("イベント一覧取得エラー: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * 初期表示用イベントを取得します。
     * 
     * 受付期間内のイベントを最優先とし、複数ある場合は対象開始日が最新のものを取得します。
     *
     * @return 初期表示用イベント
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public ShiftApplicationEvent getOldestEvent() {
        try {
            List<ShiftApplicationEvent> eventList = shiftApplicationEventRepository.findAll();
            if (eventList == null || eventList.isEmpty()) {
                return null;
            }

            LocalDate today = LocalDate.now();

            // ① 本日が受付期間内（applicationStartDate 〜 applicationEndDate）のイベントを最優先
            //    複数存在する場合は targetStartDate が最新（降順）のものを選択
            ShiftApplicationEvent activeEvent = eventList.stream()
                    .filter(e -> e.getApplicationStartDate() != null && e.getApplicationEndDate() != null)
                    .filter(e -> !today.isBefore(e.getApplicationStartDate()) && !today.isAfter(e.getApplicationEndDate()))
                    .max(Comparator.comparing(
                            ShiftApplicationEvent::getTargetStartDate,
                            Comparator.nullsFirst(Comparator.naturalOrder())
                    ))
                    .orElse(null);

            if (activeEvent != null) {
                return activeEvent;
            }

            // ② 受付中のイベントが無い場合：これから開始する未来のイベントを優先
            ShiftApplicationEvent futureEvent = eventList.stream()
                    .filter(e -> e.getTargetStartDate() != null)
                    .filter(e -> e.getTargetStartDate().isAfter(today))
                    .min(Comparator.comparing(ShiftApplicationEvent::getTargetStartDate))
                    .orElse(null);

            if (futureEvent != null) {
                return futureEvent;
            }

            // ③ それでも該当がなければ、全イベントの中で最新のもの（リストの最後、または targetStartDate が最大）
            return eventList.stream()
                    .max(Comparator.comparing(
                            ShiftApplicationEvent::getTargetStartDate,
                            Comparator.nullsFirst(Comparator.naturalOrder())
                    ))
                    .orElse(eventList.get(eventList.size() - 1));

        } catch (DataAccessException e) {
            System.err.println("初期イベント取得エラー: " + e.getMessage());
            return null;
        }
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public ShiftApplicationEvent getCurrentEvent(Integer eventId) {
        if (eventId == null) {
            return null;
        }
        try {
            return shiftApplicationEventRepository.findById(eventId).orElse(null);
        } catch (DataAccessException e) {
            System.err.println("カレントイベント取得エラー: " + e.getMessage());
            return null;
        }
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<Shift> getShiftTable(Integer eventId) {
        if (eventId == null) {
            return Collections.emptyList();
        }
        try {
            List<Shift> list = shiftRepository.findByEventId(eventId);
            return (list != null) ? list : Collections.emptyList();
        } catch (DataAccessException e) {
            System.err.println("シフト一覧取得エラー: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<LocalDate> getTargetDateList(ShiftApplicationEvent event) {
        if (event == null || event.getStartDate() == null || event.getEndDate() == null) {
            return Collections.emptyList();
        }
        List<LocalDate> dateList = new ArrayList<>();
        LocalDate current = event.getStartDate();
        LocalDate end = event.getEndDate();

        while (!current.isAfter(end)) {
            dateList.add(current);
            current = current.plusDays(1);
        }
        return dateList;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<Users> getAllUsers() {
        try {
            List<Users> list = usersRepository.findAll();
            return (list != null) ? list : Collections.emptyList();
        } catch (DataAccessException e) {
            System.err.println("ユーザー一覧取得エラー: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public Shift getShiftDetail(Integer shiftId) {
        if (shiftId == null) {
            return null;
        }
        try {
            return shiftRepository.findById(shiftId).orElse(null);
        } catch (DataAccessException e) {
            System.err.println("シフト詳細取得エラー: " + e.getMessage());
            return null;
        }
    }

    @Transactional
    public void saveShift(Shift shift) {
        if (shift == null) {
            return;
        }
        shiftRepository.save(shift);
    }

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

        for (Users user : userList) {
            if (user == null || user.getUserId() == null) {
                continue;
            }

            List<MonthlyShiftSummaryDto> monthlyList = new ArrayList<>();
            YearMonth month = YearMonth.from(eventStart);
            YearMonth endMonth = YearMonth.from(eventEnd);

            while (!month.isAfter(endMonth)) {
                LocalDate monthStart = month.atDay(1);
                LocalDate monthEnd = month.atEndOfMonth();

                LocalDate calcStart = monthStart;
                LocalDate calcEnd = eventEnd.isBefore(monthEnd) ? eventEnd : monthEnd;

                List<Shift> monthlyShifts = shiftRepository.findByShiftDateBetween(calcStart, calcEnd);
                if (monthlyShifts == null) {
                    monthlyShifts = Collections.emptyList();
                }

                int workingDays = 0;
                long totalMinutes = 0;
                LocalDate date = calcStart;

                while (!date.isAfter(calcEnd)) {
                    final LocalDate targetDate = date;
                    boolean worked = false;
                    long minutes = 0;

                    if (!targetDate.isAfter(today)) {
                        Attendance attendance = attendanceRepository
                                .findByUserIdAndWorkDate(user.getUserId(), targetDate)
                                .orElse(null);

                        if (attendance != null && attendance.getClockIn() != null && attendance.getClockOut() != null) {
                            worked = true;
                            minutes = calculateAttendanceMinutes(attendance);
                        }
                    } else {
                        Shift shift = monthlyShifts.stream()
                                .filter(s -> user.getUserId().equals(s.getUserId()))
                                .filter(s -> targetDate.equals(s.getShiftDate()))
                                .filter(s -> Integer.valueOf(1).equals(s.getIsAvailable()))
                                .findFirst()
                                .orElse(null);

                        if (shift != null && shift.getStartTime() != null && shift.getEndTime() != null) {
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
                summaryMap.put(user.getUserId(), monthlyList);
            }
        }
        return summaryMap;
    }

    private long calculateAttendanceMinutes(Attendance attendance) {
        if (attendance == null || attendance.getClockIn() == null || attendance.getClockOut() == null) {
            return 0;
        }

        long minutes = Duration.between(attendance.getClockIn(), attendance.getClockOut()).toMinutes();
        if (minutes < 0) {
            minutes += 24 * 60;
        }
        if (attendance.getRestTime() != null) {
            minutes -= (long)(attendance.getRestTime() * 60);
        }
        if (minutes < 0) {
            minutes = 0;
        }
        return minutes;
    }

    private long calculateShiftMinutes(Shift shift) {
        if (shift == null || shift.getStartTime() == null || shift.getEndTime() == null) {
            return 0;
        }

        long minutes = Duration.between(shift.getStartTime(), shift.getEndTime()).toMinutes();
        if (minutes < 0) {
            minutes += 24 * 60;
        }
        return minutes;
    }
}