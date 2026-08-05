package com.example.userShiftRequest.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.adminshift.entity.ShiftApplicationEvent;
import com.example.adminshift.repository.ShiftApplicationEventRepository;
import com.example.adminshift.repository.ShiftRequestRepository;
import com.example.userShiftRequest.dto.ShiftRequestSelectDto;
import com.example.userShiftRequest.form.ShiftRequestSelectForm;

@Service
public class ShiftRequestSelectService {

    @Autowired
    private ShiftApplicationEventRepository eventRepository;

    @Autowired
    private ShiftRequestRepository shiftRequestRepository;

    /**
     * 引数が selectedYear のみの場合の呼び出しに対応 (オーバーロード)
     */
    public ShiftRequestSelectForm getShiftList(Integer selectedYear) {
        return getShiftList(selectedYear, null);
    }

    /**
     * シフト申請選択画面一覧取得（受付中イベントのみ抽出）
     */
    public ShiftRequestSelectForm getShiftList(Integer selectedYear, String currentUserId) {

        System.out.println("検索年度 = " + selectedYear);
        System.out.println("ログインユーザーID = " + currentUserId);

        ShiftRequestSelectForm form = new ShiftRequestSelectForm();
        form.setSelectedYear(selectedYear);

        // 年度ドロップダウン用リスト取得
        List<Integer> yearList = eventRepository.findEventYears();
        form.setYearList(yearList);

        // 対象年度のイベント取得
        List<ShiftApplicationEvent> eventList;
        if (selectedYear != null) {
            eventList = eventRepository.findByTargetStartYear(selectedYear);
        } else {
            eventList = eventRepository.findAll();
        }

        List<ShiftRequestSelectDto> shiftList = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (ShiftApplicationEvent event : eventList) {

            // ① 年度絞り込みチェック
            if (selectedYear != null && event.getTargetStartDate() != null) {
                if (event.getTargetStartDate().getYear() != selectedYear) {
                    continue;
                }
            }

            // ② 受付中判定（日付期間、またはステータス名）
            boolean isPeriodActive = false;
            if (event.getApplicationStartDate() != null && event.getApplicationEndDate() != null) {
                isPeriodActive = !today.isBefore(event.getApplicationStartDate()) 
                              && !today.isAfter(event.getApplicationEndDate());
            }

            boolean isStatusActive = "受付中".equals(event.getStatusName());

            if (!isPeriodActive && !isStatusActive) {
                continue; // 受付中でなければスキップ
            }

            // DTOの作成
            ShiftRequestSelectDto dto = new ShiftRequestSelectDto();
            dto.setEventId(event.getEventId());
            dto.setTargetPeriod(event.getDisplayName());
            dto.setStartDate("受付中");

            if (event.getApplicationEndDate() != null) {
                dto.setDeadlineDate(event.getApplicationEndDate().toString());
            }

            // ③ 提出状態判定（ログインユーザーIDがある場合のみ判定）
            if (currentUserId != null && event.getEventId() != null) {
                boolean submitted = shiftRequestRepository.existsByIdUserIdAndIdEventId(
                        currentUserId, 
                        event.getEventId());
                dto.setSubmissionStatus(submitted ? "提出" : "未提出");
            } else {
                dto.setSubmissionStatus("未提出");
            }

            shiftList.add(dto);
        }

        form.setShiftList(shiftList);
        return form;
    }
}