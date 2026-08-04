package com.example.adminshift.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "shift_request_detail")
public class ShiftRequestDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_detail_id")
    private Integer requestDetailId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "event_id", nullable = false)
    private Integer eventId;

    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable;

    @Column(name = "requested_start_time")
    private LocalTime requestedStartTime;

    @Column(name = "requested_end_time")
    private LocalTime requestedEndTime;

}