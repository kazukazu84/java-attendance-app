package com.example.main.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class LogDto {
	// Logテーブル
    private Integer logId;
    private String formattedLogMessage; // 組み立て済みのログ文章（例：「山田太郎さんが出勤しました」）
    private LocalDateTime createdAt;     // 表示時の並び替えやフォーマットに利用
    private Integer targetUserId;
    
    // LogMessageテーブル
    private Integer messageId;
    private Integer messageScope;
    private Integer messageValue;
}
