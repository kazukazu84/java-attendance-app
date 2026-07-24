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
public class MessageDto {
    private Long messageId;
    private String senderId;
    private String senderName;   // 追加
    private String receiverId;
    private String receiverName; // 追加
    private String content;
    private LocalDateTime createdAt;
    private boolean isMine;
}

