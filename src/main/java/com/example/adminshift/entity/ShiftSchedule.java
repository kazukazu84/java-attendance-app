package com.example.adminshift.entity;

import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "shift_schedule")
public class ShiftSchedule {

    @EmbeddedId
    private ShiftScheduleId id;

    @Column(name = "selected_start_time")
    private LocalTime selectedStartTime;

    @Column(name = "selected_end_time")
    private LocalTime selectedEndTime;

    @Column(name = "is_scheduled")
    private Boolean isScheduled;

}