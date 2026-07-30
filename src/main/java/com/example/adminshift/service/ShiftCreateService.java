
package com.example.adminshift.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.adminshift.dto.MonthlyShiftSummaryDto;
import com.example.adminshift.entity.Shift;
import com.example.adminshift.entity.ShiftApplicationEvent;
import com.example.adminshift.entity.Users;
import com.example.adminshift.repository.ShiftApplicationEventRepository;
import com.example.adminshift.repository.ShiftRepository;
import com.example.adminshift.repository.UsersRepository;

import lombok.RequiredArgsConstructor;

/**
 * シフト作成画面のビジネスロジックを提供するサービス
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShiftCreateService {

    private final ShiftApplicationEventRepository shiftApplicationEventRepository;
    private final ShiftRepository shiftRepository;
    private final UsersRepository usersRepository;

    /**
     * すべてのイベントリストを取得します（プルダウン表示用）
     *
     * @return イベントのリスト（targetStartDate昇順、eventId昇順）
     */
    public List<ShiftApplicationEvent> getEventList() {
        return shiftApplicationEventRepository
                .findAllByOrderByTargetStartDateAscEventIdAsc();
    }

    /**
     * eventIdが最も大きい（最新作成）イベントを取得します
     *
     * @return 最新のイベント情報
     */
    public ShiftApplicationEvent getLatestEvent() {
        return shiftApplicationEventRepository
                .findTopByOrderByEventIdDesc()
                .orElse(null);
    }

    /**
     * 指定されたIDのイベント情報を取得します
     *
     * @param eventId イベントID
     * @return イベント情報（存在しない場合はnull）
     */
    public ShiftApplicationEvent getCurrentEvent(Integer eventId) {

        if (eventId == null) {
            return null;
        }

        return shiftApplicationEventRepository
                .findById(eventId)
                .orElse(null);
    }

    /**
     * 指定されたイベントIDに紐づくシフト表データを取得します
     *
     * @param eventId イベントID
     * @return シフトのリスト
     */
    public List<Shift> getShiftTable(Integer eventId) {

        if (eventId == null) {
            return List.of();
        }

        return shiftRepository.findByEventId(eventId);
    }

    /**
     * イベントの対象期間
     * （targetStartDate ～ targetEndDate）の日付一覧を生成します
     *
     * @param event 対象イベント
     * @return 日付のリスト
     */
    public List<LocalDate> getTargetDateList(
            ShiftApplicationEvent event) {

        if (event == null
                || event.getTargetStartDate() == null
                || event.getTargetEndDate() == null) {

            return List.of();
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
     * Usersテーブルから全ユーザー一覧を取得します
     *
     * @return ユーザーのリスト
     */
    public List<Users> getAllUsers() {
        return usersRepository.findAll();
    }

    /**
     * ポップアップ表示用に単一のシフト詳細情報を取得します
     *
     * @param shiftId シフトID
     * @return シフト詳細情報（存在しない場合はnull）
     */
    public Shift getShiftDetail(Integer shiftId) {

        if (shiftId == null) {
            return null;
        }

        return shiftRepository
                .findById(shiftId)
                .orElse(null);
    }

    /**
     * シフト情報を保存・更新します
     *
     * @param shift 保存対象のシフトエンティティ
     * @return 保存後のシフトエンティティ
     */
    @Transactional
    public Shift saveShift(Shift shift) {
        return shiftRepository.save(shift);
    }

    /**
     * ユーザーごとの月間勤務集計を取得します。
     *
     * @param shiftList シフト一覧
     * @param userId ユーザーID
     * @return 月別勤務集計
     */
    public List<MonthlyShiftSummaryDto> getMonthlyShiftSummary(
            List<Shift> shiftList,
            String userId) {

        /*
         * シフト一覧、またはユーザーIDがない場合
         */
        if (shiftList == null
                || shiftList.isEmpty()
                || userId == null) {

            return List.of();
        }

        /*
         * 年月ごとに集計
         */
        Map<YearMonth, MonthlyShiftSummaryDto> summaryMap =
                new HashMap<>();

        /*
         * シフトを1件ずつ確認
         */
        for (Shift shift : shiftList) {

            /*
             * 対象ユーザーのみ
             */
            if (!userId.equals(shift.getUserId())) {
                continue;
            }

            /*
             * 休みは集計しない
             *
             * isAvailable = 1 → 出勤
             * isAvailable = 0 → 休み
             */
            if (!Integer.valueOf(1).equals(
                    shift.getIsAvailable())) {

                continue;
            }

            /*
             * 日付・開始・終了時刻がない場合は集計しない
             */
            if (shift.getShiftDate() == null
                    || shift.getStartTime() == null
                    || shift.getEndTime() == null) {

                continue;
            }

            /*
             * 開始時刻と終了時刻が同じ場合
             *
             * Validatorで登録できない想定ですが、
             * DBに既存データがある場合も安全に除外します。
             */
            if (shift.getStartTime().equals(
                    shift.getEndTime())) {

                continue;
            }

            /*
             * シフト日から年月を取得
             */
            YearMonth yearMonth =
                    YearMonth.from(shift.getShiftDate());

            /*
             * その年月の集計情報を取得
             *
             * まだ存在しない場合は新規作成
             */
            MonthlyShiftSummaryDto summary =
                    summaryMap.computeIfAbsent(
                            yearMonth,
                            key -> new MonthlyShiftSummaryDto(
                                    key,
                                    0,
                                    0
                            )
                    );

            /*
             * 勤務日数 +1
             */
            summary.setWorkingDays(
                    summary.getWorkingDays() + 1
            );

            /*
             * 勤務時間を計算
             */
            long minutes =
                    calculateWorkMinutes(shift);

            /*
             * 月間勤務時間へ加算
             */
            summary.setTotalMinutes(
                    summary.getTotalMinutes() + minutes
            );
        }

        /*
         * 年月順に並べて返却
         */
        return summaryMap.values()
                .stream()
                .sorted(
                        java.util.Comparator.comparing(
                                MonthlyShiftSummaryDto::getYearMonth
                        )
                )
                .toList();
    }

    /**
     * ユーザーごとの月間勤務集計Mapを作成します。
     *
     * ThymeleafのHTMLから、
     *
     * monthlySummaryMap[user.userId]
     *
     * の形式で参照できるMapを作成します。
     *
     * @param shiftList シフト一覧
     * @param userList ユーザー一覧
     * @return ユーザーIDをキーとした月間勤務集計Map
     */
    public Map<String, List<MonthlyShiftSummaryDto>>
            getMonthlySummaryMap(
                    List<Shift> shiftList,
                    List<Users> userList) {

        /*
         * ユーザーID → 月間集計一覧
         */
        Map<String, List<MonthlyShiftSummaryDto>>
                monthlySummaryMap =
                        new HashMap<>();

        /*
         * ユーザー一覧がない場合
         */
        if (userList == null || userList.isEmpty()) {
            return monthlySummaryMap;
        }

        /*
         * ユーザーごとに月間集計を作成
         */
        for (Users user : userList) {

            /*
             * ユーザー情報が不正な場合はスキップ
             */
            if (user == null
                    || user.getUserId() == null) {

                continue;
            }

            /*
             * 対象ユーザーの月間勤務集計を取得
             */
            List<MonthlyShiftSummaryDto> summaryList =
                    getMonthlyShiftSummary(
                            shiftList,
                            user.getUserId()
                    );

            /*
             * Mapへ格納
             */
            monthlySummaryMap.put(
                    user.getUserId(),
                    summaryList
            );
        }

        return monthlySummaryMap;
    }

    /**
     * 1シフトの勤務時間を計算します。
     *
     * 休憩時間は1時間固定で控除します。
     *
     * 通常勤務：
     * 09:00～18:00
     * → 9時間
     * → 休憩1時間を控除
     * → 8時間
     *
     * 夜勤：
     * 23:00～07:00
     * → 23:00～24:00 = 1時間
     * → 00:00～07:00 = 7時間
     * → 合計8時間
     * → 休憩1時間を控除
     * → 7時間
     *
     * @param shift シフト
     * @return 勤務時間（分）
     */
    private long calculateWorkMinutes(Shift shift) {

        LocalTime start = shift.getStartTime();
        LocalTime end = shift.getEndTime();

        /*
         * 開始時刻・終了時刻がない場合
         */
        if (start == null || end == null) {
            return 0;
        }

        /*
         * 開始時刻と終了時刻が同じ場合
         */
        if (start.equals(end)) {
            return 0;
        }

        /*
         * 基準日を設定
         */
        LocalDate baseDate = LocalDate.of(2000, 1, 1);

        /*
         * 開始日時
         */
        java.time.LocalDateTime startDateTime =
                java.time.LocalDateTime.of(
                        baseDate,
                        start
                );

        /*
         * 終了日時
         */
        java.time.LocalDateTime endDateTime =
                java.time.LocalDateTime.of(
                        baseDate,
                        end
                );

        /*
         * 終了時刻が開始時刻より前の場合
         * 日付をまたぐ勤務（夜勤）
         *
         * 例：
         * 23:00 ～ 07:00
         */
        if (end.isBefore(start)) {
            endDateTime = endDateTime.plusDays(1);
        }

        /*
         * 勤務時間を分で計算
         */
        long minutes =
                java.time.Duration.between(
                        startDateTime,
                        endDateTime
                ).toMinutes();

        /*
         * 休憩1時間を控除
         */
        minutes -= 60;

        /*
         * マイナスにならないようにする
         */
        return Math.max(minutes, 0);
    }
}

