package com.example.adminshift.entity;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.persistence.Embeddable;

import lombok.Data;

@Data
@Embeddable
public class ShiftScheduleId implements Serializable {

    private String userId;

    private Integer eventId;

    private LocalDate workDate;

}