package com.example.userShiftRequest.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.adminshift.entity.ShiftApplicationEvent;
import com.example.adminshift.repository.ShiftApplicationEventRepository;
import com.example.userShiftRequest.dto.ShiftRequestSelectDto;
import com.example.userShiftRequest.form.ShiftRequestSelectForm;

@Service
public class ShiftRequestSelectService {
	
	@Autowired
	private ShiftApplicationEventRepository repository;

    public ShiftRequestSelectForm getShiftList
    (Integer selectedYear) {
    	
    	System.out.println(repository.findAll().size());    	
    	
    	// 動作確認用
    	System.out.println(
    			"検索年度 = " + selectedYear);
    	
    	ShiftRequestSelectForm form =
    			new ShiftRequestSelectForm();
    	
    	form.setSelectedYear(selectedYear);
    	
    	
    	
    	List<ShiftRequestSelectDto> shiftList =
    	        new ArrayList<>();

//    	List<ShiftApplicationEvent> eventList =
//    	        repository.findAll();
    	
    	//年度を取るためにメソッドを変更(桝田)
    	List<Integer> yearList = repository.findEventYears();
    	form.setYearList(yearList);

    	List<ShiftApplicationEvent> eventList =
    	        repository.findByTargetStartYear(selectedYear);

    	for (ShiftApplicationEvent event : eventList) {
    		
    		System.out.println(
    				event.getTargetStartDate().getYear());
    		
    		if (selectedYear != null
    		        && event.getTargetStartDate().getYear()
    		           != selectedYear) {
    		    continue;
    		}

    	    ShiftRequestSelectDto dto =
    	            new ShiftRequestSelectDto();

    	    dto.setEventId(event.getEventId());

    	    dto.setTargetPeriod(
    	            event.getDisplayName());

    	    dto.setStartDate(
    	            event.getStatusName());
    	    
    	    dto.setDeadlineDate(
    	    		event.getApplicationEndDate()
    	    		.toString());

    	    shiftList.add(dto);
    	}

    	form.setShiftList(shiftList);
    	
    	// TODO
    	// 管理者側機能連携後はイベントテーブルから取得する
    	// 現在は画面確認用のダミーデータ
    	
    	/*【後で消す】ShiftRequestSelectDto dto1 =
    			new ShiftRequestSelectDto();
    	*/
    	
    	/*----------------------------------------------
    	 * 【管理者チーム連携待ち】
    	 * ・イベント一覧は現在ダミーデータ
    	 * ・selectedYearを条件にDB検索予定
    	 * ・提出状態はuserId + eventIdで判定予定
    	 * ・シフト一覧は管理者作成データを利用予定
    	 ----------------------------------------------*/
    	
    	// ここからダミーデータ↓（※後で変更する）
    	
    	/*
    	 * dto1.setEventId(1);
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
    	*/   	
    	form.setShiftList(shiftList);
    	
    	
    	return form;
    }
}