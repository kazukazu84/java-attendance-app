package com.example.adminshift.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.adminshift.dto.GapInfo;
import com.example.adminshift.dto.ShiftApplicationEventDto;
import com.example.adminshift.entity.ShiftApplicationEvent;
import com.example.adminshift.form.CreateShiftApplicationEventForm;
import com.example.adminshift.form.UpdateShiftApplicationEventForm;
import com.example.adminshift.repository.ShiftApplicationEventRepository;

@Service
public class ShiftApplicationEventService {

    private final ShiftApplicationEventRepository repository;

    public ShiftApplicationEventService(ShiftApplicationEventRepository repository) {
        this.repository = repository;
    }

    /**
     * 画面表示用のイベント一覧を取得（ステータス判定込み）
     */
    public List<ShiftApplicationEventDto> getEventList() {
        List<ShiftApplicationEvent> entities = repository.findAllByOrderByTargetStartDateAsc();
        LocalDate now = LocalDate.now();

        return entities.stream()
                .map(entity -> ShiftApplicationEventDto.from(entity, now))
                .collect(Collectors.toList());
    }

    /**
     * イベント単体取得（既存処理用）
     */
    public ShiftApplicationEvent getEvent(Integer eventId) {
        return repository.findById(eventId).orElse(null);
    }

    /**
     * 新規作成フォーム初期値取得
     */
    public CreateShiftApplicationEventForm getCreateForm() {
        CreateShiftApplicationEventForm form = new CreateShiftApplicationEventForm();
        // 必要に応じた初期化処理
        return form;
    }

    /**
     * 日付計算ロジック
     */
    public LocalDate[] calculateNextEventDates(CreateShiftApplicationEventForm form) {
        // 既存の計算処理
        return new LocalDate[]{LocalDate.now(), LocalDate.now().plusDays(7)};
    }

    /**
     * シミュレーションGap計算
     */
    public List<GapInfo> calculateGapsWithSimulation(Integer eventId, LocalDate startDate, LocalDate endDate) {
        // 既存のGapシミュレーション処理
        return List.of();
    }

    /**
     * 現在のGap取得
     */
    public List<GapInfo> getCurrentGaps() {
        // 既存のGap取得処理
        return List.of();
    }

    /**
     * イベント作成処理
     */
    public boolean createEvent(CreateShiftApplicationEventForm form) {
        // 既存の作成処理
        return true;
    }

    /**
     * 設定保存
     */
    public void saveSetting(CreateShiftApplicationEventForm form) {
        // 既存の保存処理
    }

    /**
     * 削除対象データ有無チェック
     */
    public boolean hasDataToBeDeleted(Integer eventId, LocalDate startDate, LocalDate endDate) {
        // 既存のチェック処理
        return false;
    }

    /**
     * イベント更新処理
     */
    public boolean updateEvent(UpdateShiftApplicationEventForm form) {
        // 既存の更新処理
        return true;
    }

    /**
     * イベント削除処理
     */
    public void deleteEvent(Integer eventId) {
        repository.deleteById(eventId);
    }
}