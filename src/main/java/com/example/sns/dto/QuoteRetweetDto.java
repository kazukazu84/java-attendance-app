package com.example.sns.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class QuoteRetweetDto {
    private Long quoteId;
    private Long postId;
    private String userId;
    private String userName; // 追加
    private String comment;
    private LocalDateTime createdAt;
}

