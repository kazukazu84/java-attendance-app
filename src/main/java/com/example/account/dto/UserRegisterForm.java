/*
 * ファイルパス: src/main/java/com/example/account/dto/UserRegisterForm.java
 */

package com.example.account.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class UserRegisterForm {
    private String userId;
    private String password;
    private String userName;
    private String position; 
    
    // Integerの理由
    // 初期値を0にし、null許容しないプルダウン形式に対応するため。
    private Integer wageId; 
   
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate; 
    
    private boolean isEmploymentInsurance;
    private int isActive;
}