package com.example.sns.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class RetweetDto {
    private Long retweetId;
    private Long postId;
    private String userId;
    private String userName; // 追加
    private LocalDateTime createdAt;
}

