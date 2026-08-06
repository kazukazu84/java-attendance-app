package com.example.userShiftRequest.service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.adminshift.entity.ShiftApplicationEvent;
import com.example.adminshift.repository.ShiftApplicationEventRepository;
import com.example.adminshift.repository.ShiftRequestDetailRepository;
import com.example.userShiftRequest.dto.ShiftSelectDto;
import com.example.userShiftRequest.form.ShiftSelectForm;

/**
 * シフト申請選択画面用サービス
 */
@Service
public class ShiftRequestSelectService {

    @Autowired
    private ShiftApplicationEventRepository eventRepository;

    @Autowired
    private ShiftRequestDetailRepository detailRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d");

    /**
     * 指定された年度およびユーザーIDに基づくシフト申請一覧データの取得（受付中のみ）
     */
    public ShiftSelectForm getShiftSelectList(int selectedYear, String currentUserId) {

    	ShiftSelectForm form = new ShiftSelectForm();

    	form.setSelectedYear(selectedYear);

    	/*
    	 * 年度プルダウン
    	 */
    	form.setYearList(
    	        eventRepository.findEventYears());

        // 対象イベント一覧を取得
        List<ShiftApplicationEvent> events = eventRepository.findAll();
        List<ShiftSelectDto> shiftList = new ArrayList<>();

        for (ShiftApplicationEvent event : events) {
            
            // 【修正箇所】選択された年度 かつ 受付状態が「受付中」のものだけを抽出
        	if (event.getTargetStartDate() != null
        	        && getFiscalYear(event.getTargetStartDate()) == selectedYear
        	        && "受付中".equals(event.getStatusName())) {// ⇐ ここを追加！
                
                ShiftSelectDto dto = new ShiftSelectDto();
                dto.setEventId(event.getEventId());

                // 対象期間のフォーマット設定（例: 12/22～12/29）
                if (event.getDisplayName() != null && !event.getDisplayName().isEmpty()) {
                    dto.setTargetPeriod(event.getDisplayName());
                } else if (event.getTargetStartDate() != null && event.getTargetEndDate() != null) {
                    String period = event.getTargetStartDate().format(DATE_FORMATTER) + "～" 
                                  + event.getTargetEndDate().format(DATE_FORMATTER);
                    dto.setTargetPeriod(period);
                }

                // 締め日（applicationEndDate）のフォーマット設定（例: 12/21）
                if (event.getApplicationEndDate() != null) {
                    dto.setDeadlineDate(event.getApplicationEndDate().format(DATE_FORMATTER));
                } else {
                    dto.setDeadlineDate("-");
                }

                // 【提出・未提出の判定】
                boolean isSubmitted = false;
                if (currentUserId != null) {
                    isSubmitted = detailRepository.existsByEventIdAndUserId(event.getEventId(), currentUserId);
                }

                if (isSubmitted) {
                    dto.setSubmissionStatus("提出");
                } else {
                    dto.setSubmissionStatus("未提出");
                }

                shiftList.add(dto);
            }
        }

        form.setShiftList(shiftList);
        return form;
    }
    
    /**
     * 日付から年度（4月～翌3月）を取得
     */
    private int getFiscalYear(java.time.LocalDate date) {

        if (date.getMonthValue() >= 4) {
            return date.getYear();
        }

        return date.getYear() - 1;
    }
}