package com.example.adminshift.service;

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
    public Map<String, List<MonthlyShiftSummaryDto>> getMonthlySummaryMap(
            List<Shift> shiftList,
            List<Users> userList) {

        Map<String, List<MonthlyShiftSummaryDto>> summaryMap = new LinkedHashMap<>();

        if (userList == null || userList.isEmpty()) {
            return summaryMap;
        }

        for (Users user : userList) {
            if (user == null || user.getUserId() == null) {
                continue;
            }

            // 該当ユーザーの出勤（isAvailable == 1）かつ時間が揃っているシフトのみを抽出
            List<Shift> userShifts = (shiftList == null) ? Collections.emptyList() :
                    shiftList.stream()
                            .filter(s -> s != null && user.getUserId().equals(s.getUserId()))
                            .filter(s -> Integer.valueOf(1).equals(s.getIsAvailable()))
                            .filter(s -> s.getShiftDate() != null && s.getStartTime() != null && s.getEndTime() != null)
                            .toList();

            // 年月 (YearMonth) ごとにグループ化
            Map<YearMonth, List<Shift>> shiftsByMonth = new LinkedHashMap<>();

            for (Shift s : userShifts) {
                YearMonth ym = YearMonth.from(s.getShiftDate());
                shiftsByMonth.computeIfAbsent(ym, k -> new ArrayList<>()).add(s);
            }

            List<MonthlyShiftSummaryDto> summaryList = new ArrayList<>();

            for (Map.Entry<YearMonth, List<Shift>> entry : shiftsByMonth.entrySet()) {
                YearMonth yearMonth = entry.getKey();
                List<Shift> monthShifts = entry.getValue();
                int workingDays = monthShifts.size();
                long totalMinutes = 0;

                for (Shift s : monthShifts) {
                    long minutes = java.time.Duration.between(s.getStartTime(), s.getEndTime()).toMinutes();
                    if (minutes < 0) {
                        // 日をまたぐ夜勤シフト対応 (+24時間)
                        minutes += 24 * 60;
                    }
                    totalMinutes += minutes;
                }

                summaryList.add(new MonthlyShiftSummaryDto(yearMonth, workingDays, totalMinutes));
            }

            summaryMap.put(user.getUserId(), summaryList);
        }

        return summaryMap;
    }
}