package com.example.adminshift.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "shift_application_event")
public class ShiftApplicationEvent {

    /** イベントID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Integer eventId;

    /** 対象期間開始日 */
    @Column(name = "target_start_date", nullable = false)
    private LocalDate targetStartDate;

    /** 対象期間終了日 */
    @Column(name = "target_end_date", nullable = false)
    private LocalDate targetEndDate;

    /** 受付開始日 */
    @Column(name = "application_start_date", nullable = false)
    private LocalDate applicationStartDate;

    /** 受付終了日 */
    @Column(name = "application_end_date", nullable = false)
    private LocalDate applicationEndDate;

}

