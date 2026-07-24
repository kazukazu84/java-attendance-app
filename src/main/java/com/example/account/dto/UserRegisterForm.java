package com.example.account.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class UserRegisterForm {

    @NotBlank(message = "ユーザーIDは必須入力です。")
    @Size(min = 4, max = 20, message = "ユーザーIDは4文字以上20文字以内で入力してください。")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "ユーザーIDは半角英数字、ハイフン、アンダーバーのみで入力してください。")
    private String userId;

    // パスワードは「新規登録時のみ必須」とするため、相関バリデーションか、Controller/Service側での個別チェックで制御します。
    // ここではアノテーションを付与せず、サービスやコントローラーで空文字チェックを行います。
    private String password;

    @NotBlank(message = "名前は必須入力です。")
    @Size(max = 50, message = "名前は50文字以内で入力してください。")
    private String userName;

    @NotBlank(message = "役職は必須入力です。")
    private String position; 

    @NotNull(message = "時給は選択必須です。")
    private Integer wageId; 

    @NotNull(message = "生年月日は必須入力です。")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate; 

    private boolean isEmploymentInsurance;
    
    private int isActive;
}

