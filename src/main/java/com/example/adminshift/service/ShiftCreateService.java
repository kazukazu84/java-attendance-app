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


    /**
     * シフト申請イベント情報取得用
     */
    private final ShiftApplicationEventRepository shiftApplicationEventRepository;


    /**
     * シフト情報取得・保存用
     */
    private final ShiftRepository shiftRepository;


    /**
     * ユーザー情報取得用
     */
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

            List<ShiftApplicationEvent> list =
                    shiftApplicationEventRepository.findAll();

            return (list != null)
                    ? list
                    : Collections.emptyList();


        } catch (DataAccessException e) {

            System.err.println(
                    "イベント一覧取得エラー: "
                    + e.getMessage()
            );

            return Collections.emptyList();
        }
    }


    /**
     * 初期表示用イベントを取得します。
     *
     * 現在受付中のイベントの中から
     * 対象開始日が一番早いものを取得します。
     *
     * @return 初期表示イベント
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public ShiftApplicationEvent getOldestEvent() {


        try {

            LocalDate today =
                    LocalDate.now();


            List<ShiftApplicationEvent> eventList =
                    shiftApplicationEventRepository.findAll();


            if (eventList == null
                    || eventList.isEmpty()) {

                return null;
            }


            return eventList.stream()

                    /*
                     * 受付開始日チェック
                     */
                    .filter(e ->
                            e.getApplicationStartDate() != null)

                    /*
                     * 受付終了日チェック
                     */
                    .filter(e ->
                            e.getApplicationEndDate() != null)

                    /*
                     * 現在受付期間内
                     */
                    .filter(e ->
                            !today.isBefore(
                                    e.getApplicationStartDate()
                            )
                            &&
                            !today.isAfter(
                                    e.getApplicationEndDate()
                            ))

                    /*
                     * 対象開始日が早い順
                     */
                    .sorted(
                            (e1, e2) ->
                                    e1.getTargetStartDate()
                                      .compareTo(
                                              e2.getTargetStartDate()
                                      )
                    )

                    .findFirst()
                    .orElse(null);


        } catch (DataAccessException e) {


            System.err.println(
                    "初期イベント取得エラー: "
                    + e.getMessage()
            );


            return null;
        }
    }


    /**
     * 指定イベントIDのイベント情報を取得します。
     *
     * @param eventId イベントID
     * @return イベント情報
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public ShiftApplicationEvent getCurrentEvent(
            Integer eventId) {


        if (eventId == null) {

            return null;
        }


        try {

            return shiftApplicationEventRepository
                    .findById(eventId)
                    .orElse(null);


        } catch (DataAccessException e) {


            System.err.println(
                    "カレントイベント取得エラー: "
                    + e.getMessage()
            );


            return null;
        }
    }
    
    /**
     * 指定イベントIDのシフト一覧を取得します。
     *
     * @param eventId イベントID
     * @return シフト一覧
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<Shift> getShiftTable(Integer eventId) {


        if (eventId == null) {

            return Collections.emptyList();
        }


        try {

            List<Shift> list =
                    shiftRepository.findByEventId(eventId);


            return (list != null)
                    ? list
                    : Collections.emptyList();


        } catch (DataAccessException e) {


            System.err.println(
                    "シフト一覧取得エラー: "
                    + e.getMessage()
            );


            return Collections.emptyList();
        }
    }



    /**
     * イベント期間の日付一覧を取得します。
     *
     * @param event 対象イベント
     * @return 日付一覧
     */
    public List<LocalDate> getTargetDateList(
            ShiftApplicationEvent event) {


        if (event == null
                || event.getStartDate() == null
                || event.getEndDate() == null) {


            return Collections.emptyList();
        }


        List<LocalDate> dateList =
                new ArrayList<>();


        LocalDate current =
                event.getStartDate();


        LocalDate end =
                event.getEndDate();



        while (!current.isAfter(end)) {


            dateList.add(current);


            current =
                    current.plusDays(1);
        }


        return dateList;
    }



    /**
     * 全ユーザーを取得します。
     *
     * @return ユーザー一覧
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<Users> getAllUsers() {


        try {


            List<Users> list =
                    usersRepository.findAll();


            return (list != null)
                    ? list
                    : Collections.emptyList();



        } catch (DataAccessException e) {


            System.err.println(
                    "ユーザー一覧取得エラー: "
                    + e.getMessage()
            );


            return Collections.emptyList();
        }
    }



    /**
     * シフト詳細情報を取得します。
     *
     * @param shiftId シフトID
     * @return シフト情報
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public Shift getShiftDetail(
            Integer shiftId) {


        if (shiftId == null) {

            return null;
        }


        try {


            return shiftRepository
                    .findById(shiftId)
                    .orElse(null);



        } catch (DataAccessException e) {


            System.err.println(
                    "シフト詳細取得エラー: "
                    + e.getMessage()
            );


            return null;
        }
    }



    /**
     * シフトを新規登録または更新します。
     *
     * @param shift 保存対象シフト
     */
    @Transactional
    public void saveShift(
            Shift shift) {


        if (shift == null) {

            return;
        }


        shiftRepository.save(shift);
    }
    
    /**
     * ユーザーごと・月ごとの勤務合計を取得します。
     *
     * 同月内の過去イベント（1日〜）のデータも合算して集計します。
     * 過去日：attendanceテーブル
     * 未来日：shiftsテーブル
     *
     * @param eventId イベントID
     * @param userList ユーザー一覧
     * @return ユーザー別月間集計
     */
    public Map<String, List<MonthlyShiftSummaryDto>> getMonthlySummaryMap(
            Integer eventId,
            List<Users> userList) {

        Map<String, List<MonthlyShiftSummaryDto>> summaryMap = new LinkedHashMap<>();

        if (eventId == null || userList == null || userList.isEmpty()) {
            return summaryMap;
        }

        /*
         * 対象イベント取得
         */
        ShiftApplicationEvent event = shiftApplicationEventRepository
                .findById(eventId)
                .orElse(null);

        if (event == null || event.getTargetStartDate() == null || event.getTargetEndDate() == null) {
            return summaryMap;
        }

        LocalDate eventStart = event.getTargetStartDate();
        LocalDate eventEnd = event.getTargetEndDate();

        LocalDate today = LocalDate.now();

        /*
         * ユーザーごと集計
         */
        for (Users user : userList) {

            if (user == null || user.getUserId() == null) {
                continue;
            }

            List<MonthlyShiftSummaryDto> monthlyList = new ArrayList<>();

            YearMonth month = YearMonth.from(eventStart);
            YearMonth endMonth = YearMonth.from(eventEnd);

            /*
             * イベント終了月まで繰り返す
             */
            while (!month.isAfter(endMonth)) {

                LocalDate monthStart = month.atDay(1);
                LocalDate monthEnd = month.atEndOfMonth();

                /*
                 * 【★ここを修正★】
                 * その月の集計開始日は「イベント開始日」ではなく「必ず1日(monthStart)」からにする。
                 * 集計終了日は「イベント終了日」または「月末」のどちらか早い方にする。
                 */
                LocalDate calcStart = monthStart;
                LocalDate calcEnd = eventEnd.isBefore(monthEnd) ? eventEnd : monthEnd;

                /*
                 * この月（1日〜集計終了日）のシフトをすべて取得
                 * ※eventId依存ではなく日付範囲で検索する、または同月の全イベントを対象にするリポジトリメソッドを使用
                 */
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

                    /*
                     * 過去日（今日含む）は勤怠実績を使用
                     */
                    if (!targetDate.isAfter(today)) {

                        Attendance attendance = attendanceRepository
                                .findByUserIdAndWorkDate(user.getUserId(), targetDate)
                                .orElse(null);

                        if (attendance != null
                                && attendance.getClockIn() != null
                                && attendance.getClockOut() != null) {

                            worked = true;
                            minutes = calculateAttendanceMinutes(attendance);
                        }

                    } else {

                        /*
                         * 未来日はシフト予定を使用
                         */
                        Shift shift = monthlyShifts.stream()
                                .filter(s -> user.getUserId().equals(s.getUserId()))
                                .filter(s -> targetDate.equals(s.getShiftDate()))
                                .filter(s -> Integer.valueOf(1).equals(s.getIsAvailable()))
                                .findFirst()
                                .orElse(null);

                        if (shift != null
                                && shift.getStartTime() != null
                                && shift.getEndTime() != null) {

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
                
                /*
                 * 勤務実績がある月だけ追加
                 */
                if (workingDays > 0) {

                    monthlyList.add(
                            new MonthlyShiftSummaryDto(
                                    month,
                                    workingDays,
                                    totalMinutes
                            )
                    );
                }

                /*
                 * 次の月へ
                 */
                month = month.plusMonths(1);

            } // 月ループ終了

            /*
             * ユーザー単位で登録
             */
            if (!monthlyList.isEmpty()) {

                summaryMap.put(
                        user.getUserId(),
                        monthlyList
                );
            }

        } // ユーザーループ終了

        return summaryMap;
    }
    
    /**
     * Attendanceの勤務時間を分単位で計算します。
     *
     * 計算式：
     *
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
         * restTime:
         * 時間単位
         *
         * 例：
         * 1.0 → 60分
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
     * Shift予定勤務時間を分単位で計算します。
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
         * 夜勤対応
         *
         * 例：
         *
         * 22:00 ～ 05:00
         *
         */
        if (minutes < 0) {

            minutes += 24 * 60;
        }



        return minutes;
    }

}