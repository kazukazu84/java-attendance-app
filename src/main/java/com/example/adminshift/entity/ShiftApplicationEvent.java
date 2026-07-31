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

    /**
     * 受付状態の名称を取得（表示用）
     */
    public String getStatusName() {
        LocalDate today = LocalDate.now();
        if (applicationStartDate == null || applicationEndDate == null) {
            return "";
        }
        if (today.isBefore(applicationStartDate)) {
            return "受付前";
        } else if (!today.isBefore(applicationStartDate) && !today.isAfter(applicationEndDate)) {
            return "受付中";
        } else {
            return "受付終了";
        }
    }

    /**
     * 受付状態に応じたCSSクラス名を取得（表示用）
     */
    public String getStatusCssClass() {
        LocalDate today = LocalDate.now();
        if (applicationStartDate == null || applicationEndDate == null) {
            return "";
        }
        if (today.isBefore(applicationStartDate)) {
            return "status-before";
        } else if (!today.isBefore(applicationStartDate) && !today.isAfter(applicationEndDate)) {
            return "status-open";
        } else {
            return "status-closed";
        }
    }

    // ============================================================================
    // Service層との互換性用メソッド (getStartDate / getEndDate)
    // ============================================================================

    /**
     * 互換用ゲッター（targetStartDateを返却）
     */
    public LocalDate getStartDate() {
        return this.targetStartDate;
    }

    /**
     * 互換用ゲッター（targetEndDateを返却）
     */
    public LocalDate getEndDate() {
        return this.targetEndDate;
    }
}