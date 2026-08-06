package com.example.adminshift.form;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import com.example.adminshift.validation.ValidShiftTime;

import lombok.Data;


/**
 * シフト編集・新規作成モーダル用フォーム
 */
@Data
@ValidShiftTime
public class ShiftForm {


    private Integer id;


    private Integer eventId;


    private String userId;


    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate shiftDate;


    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime startTime;


    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime endTime;



    /**
     * 備考・メモ
     */
    @Size(
        max = 255,
        message = "メモは255文字以内で入力してください"
    )
    private String memo;



    /**
     * 画面上の「休みとして設定」チェックボックス
     * true:休み
     * false:出勤
     */
    private boolean rest;



    /**
     * 出勤可能フラグ
     * 0:休み
     * 1:出勤可能
     */
    private Integer isAvailable = 1;

}