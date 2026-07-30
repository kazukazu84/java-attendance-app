package com.example.account.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

@Entity 
@Table(name = "wage")
@Data
public class Wage {

    @Id
    @Column(name = "wage_id", nullable = false)
    private int wageId; // 💡 wage_id から wageId に変更（ゲッターは getWageId() になります）
    
    @Column(name = "wage_value", nullable = false)
    private int wageValue; // 💡 wage_value から wageValue に変更
    
}