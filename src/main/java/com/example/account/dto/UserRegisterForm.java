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
    @Pattern(
        regexp = "^[a-zA-Z0-9_-]+$",
        message = "ユーザーIDは半角英数字、ハイフン、アンダーバーのみで入力してください。"
    )
    private String userId;


    /**
     * パスワード
     *
     * 新規登録時のみ必須チェックをController側で実施
     */
    private String password;


    @NotBlank(message = "名前は必須入力です。")
    @Size(max = 50, message = "名前は50文字以内で入力してください。")
    private String userName;


    @NotBlank(message = "役職は必須入力です。")
    private String position;


    @NotNull(message = "時給は選択必須です。")
    private Integer wageId;


    /**
     * 生年月日
     */
    @NotNull(message = "生年月日は必須入力です。")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;


    /**
     * 雇用保険加入
     */
    private boolean isEmploymentInsurance;


    /**
     * 在籍状態
     *
     * 1 = 有効
     * 0 = 無効
     */
    private int isActive;



    /**
     * 16歳未満チェック
     *
     * trueの場合、登録不可
     *
     * @return 16歳未満の場合 true
     */
    public boolean isUnder16() {

        if (birthDate == null) {
            return false;
        }


        LocalDate today = LocalDate.now();


        /*
         * 今日から16年前の日付
         *
         * 例:
         * 今日 2026-08-06
         * 判定基準 2010-08-06
         */
        LocalDate limitDate =
                today.minusYears(16);


        /*
         * 基準日より後なら16歳未満
         *
         * 例:
         * 2010-08-07
         * → 16歳未満
         */
        return birthDate.isAfter(limitDate);
    }



    /**
     * 未来日チェック
     *
     * @return 未来日ならtrue
     */
    public boolean isFutureBirthDate() {

        if (birthDate == null) {
            return false;
        }

        return birthDate.isAfter(LocalDate.now());
    }
}