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
public class DelayInfoDto {

    private String lineName;

    private String statusText;     // ← 追加
    private String detailText;     // ← 追加
    private String updatedText;    // ← 追加

    private int delayMinutes;
    private String reason;
    private LocalDateTime occurredAt;
}
