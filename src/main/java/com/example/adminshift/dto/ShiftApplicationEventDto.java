package com.example.adminshift.dto;

import java.time.LocalDate;

import com.example.adminshift.entity.ShiftApplicationEvent;
import com.example.adminshift.status.ShiftApplicationEventStatus;

import lombok.Data;

/**
 * 画面表示用のシフト受付期間イベントDTO
 */
@Data
public class ShiftApplicationEventDto {

    private Integer eventId;
    private LocalDate targetStartDate;
    private LocalDate targetEndDate;
    private LocalDate applicationStartDate;
    private LocalDate applicationEndDate;
    private ShiftApplicationEventStatus status;

    /**
     * Entityと現在日からDTOを生成するファクトリメソッド
     */
    public static ShiftApplicationEventDto from(ShiftApplicationEvent entity, LocalDate now) {
        ShiftApplicationEventDto dto = new ShiftApplicationEventDto();
        dto.setEventId(entity.getEventId());
        dto.setTargetStartDate(entity.getTargetStartDate());
        dto.setTargetEndDate(entity.getTargetEndDate());
        dto.setApplicationStartDate(entity.getApplicationStartDate());
        dto.setApplicationEndDate(entity.getApplicationEndDate());
        dto.setStatus(ShiftApplicationEventStatus.of(entity.getApplicationStartDate(), entity.getApplicationEndDate(), now));
        return dto;
    }

    /**
     * Thymeleaf画面表示用（${event.statusLabel} で呼び出せます）
     */
    public String getStatusLabel() {
        return status != null ? status.getLabel() : "";
    }
}