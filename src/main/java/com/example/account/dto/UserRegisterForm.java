package com.example.account.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Pattern;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class UserRegisterForm {
	// 💡 ユーザーIDに「半角英数字のみ」のバリデーションの呪文を付与！
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "ユーザーIDは半角英数字のみで入力してください。")
	private String userId;
    private String password;
    private String userName;
    private String position; 
    //private int wage;
    // Integerの理由
    // 初期値を0にし、null許容しないプルダウン形式に対応するため。
    private Integer wageId; 
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate; 
    private boolean isEmploymentInsurance;
    private int isActive;
}