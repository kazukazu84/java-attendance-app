package com.example.salary.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 時給テーブル（共通ドメイン）
 */
@Entity
@Table(name = "hourly_wage")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WageEntity {

    @Id
    @Column(name = "wage_id")
    private int wageId;

    @Column(name = "wage_value")
    private int wageValue;
}

