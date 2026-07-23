package com.example.adminshift.entity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "shift_application_event")
@Data
public class ShiftApplicationEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer eventId;

    private LocalDate targetStartDate;
    private LocalDate targetEndDate;
    private LocalDate applicationStartDate;
    private LocalDate applicationEndDate;

    /**
     * 表示用イベント期間文字列を取得（yyyy/MM/dd～yyyy/MM/dd）
     */
    public String getDisplayName() {
        if (targetStartDate == null || targetEndDate == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        return targetStartDate.format(formatter) + "～" + targetEndDate.format(formatter);
    }
}