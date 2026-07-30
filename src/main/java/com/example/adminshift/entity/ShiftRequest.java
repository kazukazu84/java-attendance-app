package com.example.adminshift.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "shift_request")
public class ShiftRequest {

    @EmbeddedId
    private ShiftRequestId id;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

}