package com.example.rail.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CertificateDto {

    private String certificateId;
    private String lineName;

    private String statusText;    // ← そのまま返す
    private String detailText;    // ← そのまま返す
    private String updatedText;   // ← そのまま返す

    private int delayMinutes;
    private String reason;

    private String issuedToUserId;
    private LocalDateTime issuedAt;
}
