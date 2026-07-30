package com.example.rail.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "delay_certificate")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DelayCertificateEntity {

    @Id
    private String certificateId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rail_line_id")
    private RailLineEntity railLine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delay_info_id")
    private DelayInfoEntity delayInfo;

    private LocalDateTime issuedAt;

    private String issuedToUserId;
}