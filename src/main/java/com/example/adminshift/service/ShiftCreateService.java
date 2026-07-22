package com.example.adminshift.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
     * @return イベントのリスト（eventId降順）
     */
    public List<ShiftApplicationEvent> getEventList() {
        return shiftApplicationEventRepository.findAllByOrderByEventIdDesc();
    }

    /**
     * eventIdが最も大きい（最新作成）イベントを取得します
     *
     * @return 最新のイベント情報
     */
    public ShiftApplicationEvent getLatestEvent() {
        return shiftApplicationEventRepository.findTopByOrderByEventIdDesc().orElse(null);
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
        return shiftApplicationEventRepository.findById(eventId).orElse(null);
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
     * イベントの対象期間（targetStartDate ～ targetEndDate）の日付一覧を生成します
     *
     * @param event 対象イベント
     * @return 日付のリスト
     */
    public List<LocalDate> getTargetDateList(ShiftApplicationEvent event) {
        if (event == null || event.getTargetStartDate() == null || event.getTargetEndDate() == null) {
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
        return shiftRepository.findById(shiftId).orElse(null);
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
}