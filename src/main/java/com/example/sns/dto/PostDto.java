package com.example.sns.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {
    private Long postId;
    private String userId;
    private String userName;
    private String content;
    private LocalDateTime createdAt;
    private long likeCount;
    private long retweetCount;
    private long quoteCount;
    private boolean likedByCurrentUser;
    private boolean retweetedByCurrentUser;
    private boolean quotedByCurrentUser; // 追加（任意）
}

