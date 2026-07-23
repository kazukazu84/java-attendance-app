package com.example.adminshift.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

/**
 * イベント情報（プルダウン選択用）を管理するエンティティクラス
 */
@Entity
@Table(name = "events")
@Data
public class Event {

    /** イベントID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** イベント名 */
    private String name;
}
