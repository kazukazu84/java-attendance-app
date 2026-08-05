package com.example.userShiftRequest.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.adminshift.entity.ShiftApplicationEvent;
import com.example.adminshift.entity.ShiftRequestDetail;
import com.example.adminshift.repository.ShiftApplicationEventRepository;
import com.example.adminshift.repository.ShiftRequestDetailRepository;
import com.example.userShiftRequest.dto.ShiftRequestDto;
import com.example.userShiftRequest.form.ShiftRequestForm;
import com.example.userShiftRequest.validation.ShiftRequestValidator;

@Service
public class ShiftRequestService {

    @Autowired
    private ShiftRequestDetailRepository repository;
    
    @Autowired
    private ShiftApplicationEventRepository eventRepository;

    private final ShiftRequestValidator validator = new ShiftRequestValidator();

    /**
     * シフト希望情報取得（対象イベントの期間日付を自動生成）
     */
    public ShiftRequestForm getShiftRequestInfo(Integer eventId, String currentUserId) {

        ShiftRequestForm form = new ShiftRequestForm();
        form.setEventId(eventId);
        
        // 対象イベント取得
        ShiftApplicationEvent event = eventRepository.findById(eventId).orElse(null);

        if (event == null) {
            System.out.println("イベントが存在しません: eventId=" + eventId);
            return form;
        }

        form.setTargetPeriod(event.getDisplayName());

        // 既存の登録データがあれば取得して日付ごとにマップ化（ユーザーIDとイベントIDで絞り込み）
        List<ShiftRequestDetail> existingDetails = new ArrayList<>();
        if (currentUserId != null) {
            // ※ repository に findByUserIdAndEventId 等がある前提。無い場合は全件取得からフィルタ
            existingDetails = repository.findAll().stream()
                    .filter(d -> eventId.equals(d.getEventId()) && currentUserId.equals(d.getUserId()))
                    .collect(Collectors.toList());
        }

        Map<LocalDate, ShiftRequestDetail> detailMap = existingDetails.stream()
                .collect(Collectors.toMap(ShiftRequestDetail::getWorkDate, d -> d, (d1, d2) -> d1));

        List<ShiftRequestDto> shiftList = new ArrayList<>();

        // イベントの対象期間（開始日〜終了日）をループして日付一覧を作る
        LocalDate startDate = event.getTargetStartDate();
        LocalDate endDate = event.getTargetEndDate();

        if (startDate != null && endDate != null) {
            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                
                ShiftRequestDto dto = new ShiftRequestDto();
                dto.setWorkDate(date.toString());

                // すでにDBに保存済みのデータがある場合はその値を表示
                if (detailMap.containsKey(date)) {
                    ShiftRequestDetail detail = detailMap.get(date);
                    dto.setAvailable(Boolean.TRUE.equals(detail.getIsAvailable()) ? "○" : "×");
                    
                    if (detail.getRequestedStartTime() != null) {
                        dto.setStartTime(detail.getRequestedStartTime().toString());
                    }
                    if (detail.getRequestedEndTime() != null) {
                        dto.setEndTime(detail.getRequestedEndTime().toString());
                    }
                } else {
                    // 新規入力時の初期値設定（デフォルト: ○）
                    dto.setAvailable("○");
                    dto.setStartTime("");
                    dto.setEndTime("");
                }

                shiftList.add(dto);
            }
        }

        form.setShiftList(shiftList);
        return form;
    }

    /**
     * シフト希望登録
     */
    public boolean applyShiftRequest(ShiftRequestForm form, String currentUserId) {

        if (form.getShiftList() == null) {
            return false;
        }

        boolean saved = false;

        for (ShiftRequestDto dto : form.getShiftList()) {

            // 入力チェック（×の場合は時刻不要など）
            if (!validator.isValid(dto)) {
                continue;
            }

            ShiftRequestDetail entity = new ShiftRequestDetail();

            entity.setUserId(currentUserId);
            entity.setEventId(form.getEventId());

            // 日付
            entity.setWorkDate(LocalDate.parse(dto.getWorkDate()));

            // 出勤可否
            boolean isAvailable = "○".equals(dto.getAvailable());
            entity.setIsAvailable(isAvailable);

            if (isAvailable) {
                // 開始時間・終了時間（空文字でない場合のみパース）
                if (dto.getStartTime() != null && !dto.getStartTime().isEmpty()) {
                    entity.setRequestedStartTime(LocalTime.parse(dto.getStartTime()));
                }
                if (dto.getEndTime() != null && !dto.getEndTime().isEmpty()) {
                    entity.setRequestedEndTime(LocalTime.parse(dto.getEndTime()));
                }
            } else {
                entity.setRequestedStartTime(null);
                entity.setRequestedEndTime(null);
            }

            repository.save(entity);
            saved = true;
        }

        System.out.println("申請処理完了: eventId=" + form.getEventId() + ", userId=" + currentUserId);
        return saved;
    }
}