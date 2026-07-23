package com.example.adminshift.form;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class ShiftForm {

    private Integer id;

    @NotNull(message = "イベントを選択してください")
    private Integer eventId;

    @NotNull(message = "対象ユーザーが未指定です")
    private String userId;

    @NotNull(message = "勤務日を入力してください")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate shiftDate;

    /** 休みフラグ（追加） */
    private boolean rest;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime endTime;

    @Size(max = 200, message = "メモは200文字以内で入力してください")
    private String memo;
}