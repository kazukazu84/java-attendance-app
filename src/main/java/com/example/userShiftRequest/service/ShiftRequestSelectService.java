package com.example.userShiftRequest.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.userShiftRequest.dto.ShiftRequestSelectDto;
import com.example.userShiftRequest.form.ShiftRequestSelectForm;

@Service
public class ShiftRequestSelectService {

    public ShiftRequestSelectForm getShiftList
    (Integer selectedYear) {
    	
    	// 動作確認用
    	System.out.println(
    			"検索年度 = " + selectedYear);
    	
    	ShiftRequestSelectForm form =
    			new ShiftRequestSelectForm();
    	
    	form.setSelectedYear(selectedYear);
    	
    	List<ShiftRequestSelectDto> shiftList =
    			new ArrayList<>();
    	
    	
    	// TODO
    	// 管理者側機能連携後はイベントテーブルから取得する
    	// 現在は画面確認用のダミーデータ
    	
    	ShiftRequestSelectDto dto1 =
    			new ShiftRequestSelectDto();
    	
    	
    	/*----------------------------------------------
    	 * 【管理者チーム連携待ち】
    	 * ・イベント一覧は現在ダミーデータ
    	 * ・selectedYearを条件にDB検索予定
    	 * ・提出状態はuserId + eventIdで判定予定
    	 * ・シフト一覧は管理者作成データを利用予定
    	 ----------------------------------------------*/
    	
    	// ここからダミーデータ↓（※後で変更する）
    	dto1.setEventId(1);
    	dto1.setSubmissionStatus("未提出");
    	dto1.setTargetPeriod("12/22～12/29");
    	dto1.setDeadlineDate("12/21");
    	dto1.setStartDate("受付中");
    	
    	shiftList.add(dto1);
    	
    	ShiftRequestSelectDto dto2 =
    			new ShiftRequestSelectDto();
    	
    	dto2.setEventId(2);
    	dto2.setSubmissionStatus("済");
    	dto2.setTargetPeriod("12/07～12/21");
    	dto2.setDeadlineDate("12/06");
    	dto2.setStartDate("開始済");
    	
    	shiftList.add(dto2);
    	
    	form.setShiftList(shiftList);
    	
    	return form;
    }
}