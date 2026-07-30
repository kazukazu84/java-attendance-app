package com.example.sns.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class LikeDto {
    private Long likeId;
    private Long postId;
    private String userId;
    private String userName; // 追加
    private LocalDateTime createdAt;
}

