package com.example.adminshift.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
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

    /**
     * 勤怠情報取得用
     *
     * 過去日の勤務時間集計で使用します。
     */
    private final AttendanceRepository attendanceRepository;
    /**
     * 全イベント一覧を取得します。
     *
     * @return イベントリスト（取得失敗時は空リスト）
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<ShiftApplicationEvent> getEventList() {
        try {
            List<ShiftApplicationEvent> list = shiftApplicationEventRepository.findAll();
            return (list != null) ? list : Collections.emptyList();
        } catch (DataAccessException e) {
            System.err.println("イベント一覧取得エラー (テーブル不在等): " + e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * 初期表示用のイベントを取得します。
     * 明日以降に開始・または本日進行中の有効なイベント（8月イベント等）を自動優先選択します。
     *
     * @return 初期選択用イベント情報
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public ShiftApplicationEvent getOldestEvent() {
        try {
            List<ShiftApplicationEvent> eventList = shiftApplicationEventRepository.findAll();
            if (eventList == null || eventList.isEmpty()) {
                return null;
            }

            LocalDate today = LocalDate.now();

            // ① 明日以降に対象期間（targetStartDate）が始まる未来のイベントを最優先（例: 2026/08/01〜）
            ShiftApplicationEvent futureEvent = eventList.stream()
                    .filter(e -> e.getTargetStartDate() != null)
                    .filter(e -> e.getTargetStartDate().isAfter(today))
                    .findFirst()
                    .orElse(null);

            if (futureEvent != null) {
                return futureEvent;
            }

            // ② 本日が受付期間内（applicationStartDate 〜 applicationEndDate）のイベントを検索
            for (ShiftApplicationEvent event : eventList) {
                if (event.getApplicationStartDate() != null && event.getApplicationEndDate() != null) {
                    boolean isStarted = !today.isBefore(event.getApplicationStartDate());
                    boolean isNotEnded = !today.isAfter(event.getApplicationEndDate());
                    if (isStarted && isNotEnded) {
                        return event;
                    }
                }
            }

            // ③ 該当がなければ最新（リストの最後）のイベントを返却
            return eventList.get(eventList.size() - 1);

        } catch (DataAccessException e) {
            System.err.println("初期イベント取得エラー: " + e.getMessage());
            return null;
        }
    }

    /**
     * 指定されたイベントIDのイベント詳細情報を取得します。
     *
     * @param eventId イベントID
     * @return イベント情報（取得失敗時は null）
     */
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

    /**
     * 指定されたイベントIDのシフト一覧を取得します。
     *
     * @param eventId イベントID
     * @return シフトリスト（取得失敗時は空リスト）
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<Shift> getShiftTable(Integer eventId) {
        if (eventId == null) {
            return Collections.emptyList();
        }
        try {
            List<Shift> list = shiftRepository.findByEventId(eventId);
            return (list != null) ? list : Collections.emptyList();
        } catch (DataAccessException e) {
            System.err.println("シフトテーブル取得エラー: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * イベントの開始日から終了日までの全日付リストを生成します。
     *
     * @param event カレントイベント情報
     * @return 日付リスト
     */
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

    /**
     * システムに登録されている全ユーザーを取得します。
     *
     * @return ユーザーリスト（取得失敗時は空リスト）
     */
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

    /**
     * シフト詳細情報を取得します。
     *
     * @param shiftId シフトID
     * @return シフト情報（取得失敗時は null）
     */
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

    /**
     * シフトの新規登録または更新を行います。
     *
     * @param shift シフト情報
     */
    @Transactional
    public void saveShift(Shift shift) {
        if (shift == null) {
            return;
        }
        shiftRepository.save(shift);
    }

    /**
     * ユーザーごと・月ごとの勤務合計（日数・時間）を集計したマップを作成します。
     *
     * @param shiftList シフトリスト
     * @param userList ユーザーリスト
     * @return Map<ユーザーID, List<MonthlyShiftSummaryDto>>
     */
    /**
     * ユーザーごと・月ごとの勤務合計（日数・時間）を集計したマップを作成します。
     *
     * 過去日はattendanceテーブルの実績、
     * 未来日はshiftsテーブルの予定を使用します。
     *
     * また、同じ年月内で複数イベントが存在する場合は、
     * すべてのイベントを合算します。
     *
     * @param eventId 現在選択中イベントID
     * @param userList ユーザーリスト
     * @return Map<ユーザーID, List<MonthlyShiftSummaryDto>>
     */
    public Map<String, List<MonthlyShiftSummaryDto>> getMonthlySummaryMap(
            Integer eventId,
            List<Users> userList) {


        Map<String, List<MonthlyShiftSummaryDto>> summaryMap =
                new LinkedHashMap<>();


        if (eventId == null
                || userList == null
                || userList.isEmpty()) {

            return summaryMap;
        }


        /*
         * 現在イベント取得
         */
        ShiftApplicationEvent currentEvent =
                shiftApplicationEventRepository
                        .findById(eventId)
                        .orElse(null);


        if (currentEvent == null) {
            return summaryMap;
        }


        /*
         * 対象年月取得
         */
        YearMonth yearMonth =
                YearMonth.from(
                        currentEvent.getTargetStartDate()
                );


        LocalDate monthStart =
                yearMonth.atDay(1);


        LocalDate monthEnd =
                yearMonth.atEndOfMonth();



        /*
         * 同じ月に存在するイベント取得
         *
         * 例：
         * 8/1～8/24
         * 8/25～8/31
         */
        List<ShiftApplicationEvent> monthlyEvents =
                shiftApplicationEventRepository
                        .findEventsOverlappingPeriod(
                                monthStart,
                                monthEnd
                        );


        if (monthlyEvents == null) {
            monthlyEvents = Collections.emptyList();
        }



        /*
         * 月内のShift取得
         */
        List<Shift> monthlyShifts =
                shiftRepository.findByShiftDateBetween(
                        monthStart,
                        monthEnd
                );


        if (monthlyShifts == null) {
            monthlyShifts = Collections.emptyList();
        }



        LocalDate today = LocalDate.now();



        /*
         * ユーザー単位集計
         */
        for (Users user : userList) {


            if (user == null
                    || user.getUserId() == null) {

                continue;
            }


            int workingDays = 0;

            long totalMinutes = 0;



            /*
             * 月の日付を1日ずつ確認
             */
            LocalDate date = monthStart;


            while (!date.isAfter(monthEnd)) {


                final LocalDate targetDate = date;


                boolean worked = false;


                long minutes = 0;


                /*
                 * 過去日
                 *
                 * attendance実績使用
                 */
                if (!date.isAfter(today)) {


                	Attendance attendance =
                	        attendanceRepository
                	                .findByUserIdAndWorkDate(
                	                        user.getUserId(),
                	                        targetDate
                	                )
                	                .orElse(null);



                    if (attendance != null
                            && attendance.getClockIn() != null
                            && attendance.getClockOut() != null) {


                        worked = true;


                        minutes =
                                calculateAttendanceMinutes(
                                        attendance
                                );
                    }


                } else {


                    /*
                     * 未来日
                     *
                     * shift予定使用
                     */
                    Shift shift =
                            monthlyShifts.stream()
                                    .filter(s ->
                                            user.getUserId()
                                                    .equals(
                                                            s.getUserId()
                                                    ))
                                    .filter(s ->
                                    targetDate.equals(
                                            s.getShiftDate()
                                    ))
                                    .filter(s ->
                                            Integer.valueOf(1)
                                                    .equals(
                                                        s.getIsAvailable()
                                                    ))
                                    .findFirst()
                                    .orElse(null);



                    if (shift != null
                            && shift.getStartTime() != null
                            && shift.getEndTime() != null) {


                        worked = true;


                        minutes =
                                calculateShiftMinutes(
                                        shift
                                );
                    }
                }



                if (worked) {

                    workingDays++;

                    totalMinutes += minutes;
                }


                date = date.plusDays(1);
            }



            /*
             * 勤務があるユーザーだけ追加
             */
            if (workingDays > 0) {


                List<MonthlyShiftSummaryDto> list =
                        new ArrayList<>();


                list.add(
                        new MonthlyShiftSummaryDto(
                                yearMonth,
                                workingDays,
                                totalMinutes
                        )
                );


                summaryMap.put(
                        user.getUserId(),
                        list
                );
            }
        }


        return summaryMap;
    }
    
    /**
     * Attendanceの勤務時間を分単位で計算します。
     *
     * 計算式：
     * 退勤時間 - 出勤時間 - 休憩時間
     *
     * @param attendance 勤怠情報
     * @return 勤務時間（分）
     */
    private long calculateAttendanceMinutes(
            Attendance attendance) {


        if (attendance == null
                || attendance.getClockIn() == null
                || attendance.getClockOut() == null) {

            return 0;
        }


        long minutes =
                Duration.between(
                        attendance.getClockIn(),
                        attendance.getClockOut()
                )
                .toMinutes();


        /*
         * 日跨ぎ勤務対応
         */
        if (minutes < 0) {

            minutes += 24 * 60;
        }


        /*
         * 休憩時間控除
         *
         * restTimeは時間単位
         * 例：1.0 → 60分
         */
        if (attendance.getRestTime() != null) {

            minutes -=
                    (long)(attendance.getRestTime() * 60);
        }


        /*
         * マイナス防止
         */
        if (minutes < 0) {

            minutes = 0;
        }


        return minutes;
    }
    
    /**
     * Shiftの予定勤務時間を分単位で計算します。
     *
     * @param shift シフト情報
     * @return 勤務時間（分）
     */
    private long calculateShiftMinutes(
            Shift shift) {


        if (shift == null
                || shift.getStartTime() == null
                || shift.getEndTime() == null) {

            return 0;
        }


        long minutes =
                Duration.between(
                        shift.getStartTime(),
                        shift.getEndTime()
                )
                .toMinutes();


        /*
         * 夜勤など日をまたぐ勤務対応
         */
        if (minutes < 0) {

            minutes += 24 * 60;
        }


        return minutes;
    }
}