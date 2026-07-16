package com.example.main.dto;


import java.time.LocalDate;
import java.time.LocalTime;

/*
 * ユーザーメイン画面へ表示するシフト情報DTO
 * 
 * ShiftScheduleテーブルの情報を
 * 画面表示用に受け渡すためのクラス
 */

public class UserShiftDto {
	
	//ユーザーID
	private Long userId;
	
    //勤務日
    private LocalDate workDate;

    //勤務開始時間
    private LocalTime startTime;

    //勤務終了時間
    private LocalTime endTime;

    //休み（true=休み、false=出勤）
    private boolean holiday;

}