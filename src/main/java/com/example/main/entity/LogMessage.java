package com.example.main.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "log_message")
@Data
public class LogMessage {

    @Id
    private Integer messageId;

    @Column(nullable = false)
    private Integer messageScope; // 0: 全員, 1: 個人+管理者

    @Column(nullable = false)
    private String messageValue;
}