package com.example.adminshift.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.adminshift.entity.Event;
import com.example.adminshift.entity.Shift;
import com.example.adminshift.repository.EventRepository;
import com.example.adminshift.repository.ShiftRepository;

import lombok.RequiredArgsConstructor;

/**
 * シフト作成画面のビジネスロジックを提供するサービス
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShiftCreateService {

    private final EventRepository eventRepository;
    private final ShiftRepository shiftRepository;

    /**
     * すべてのイベントリストを取得します（プルダウン表示用）
     *
     * @return イベントのリスト
     */
    public List<Event> getEventList() {
        return eventRepository.findAll();
    }

    /**
     * 指定されたIDのイベント情報を取得します
     *
     * @param eventId イベントID
     * @return イベント情報（存在しない場合はnull）
     */
    public Event getCurrentEvent (Integer eventId) {
        if (eventId == null) {
            return null;
        }
        return eventRepository.findById(eventId).orElse(null);
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