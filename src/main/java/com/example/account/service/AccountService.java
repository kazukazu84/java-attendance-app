/*
 * ファイルパス: src/main/java/com/example/account/service/AccountService.java
 */
package com.example.account.service;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.account.dto.UserRegisterForm;
import com.example.account.entity.Position;
import com.example.account.entity.UserInfo;
import com.example.account.entity.Wage;
import com.example.account.repository.UserInfoRepository;
import com.example.account.repository.WageRepository; // 💡 追加

@Service
public class AccountService {
    @Autowired private UserInfoRepository userRepo;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private WageRepository wageRepo; // 💡 賃金マスタ検索用にリポジトリを追加

    /**
     * IDによるユーザー検索
     */
    public Optional<UserInfo> findUserById(String id) {
        return userRepo.findById(id);
    }

    /**
     * 新規アカウント登録
     */
    @Transactional
    public void registerAccount(
        String id, 
        String rawPassword, 
        String userName, 
        Position position, 
        int wageId, // 💡 int wage から int wageId に変更
        Date birthDate, 
        int attendanceStatus, 
        boolean isEmploymentInsurance, 
        int isActive
    ) {
        UserInfo user = new UserInfo();
        
        user.setUserId(id);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setUserName(userName);
        user.setPosition(position);
        
        // 💡 画面から渡された wageId を元に、賃金マスタ（Wageエンティティ）を検索してセット
        Wage wage = wageRepo.findById(wageId)
            .orElseThrow(() -> new IllegalArgumentException("指定された賃金IDが存在しません: " + wageId));
        user.setWage(wage); // 💡 user.setWage(wage) でオブジェクトをセット
        
        user.setBirthDate(birthDate);
        user.setAttendanceStatus(attendanceStatus);
        user.setEmploymentInsurance(isEmploymentInsurance);
        user.setIsActive(isActive);
        userRepo.save(user);
    }

    /**
     * 既存アカウント更新
     * @return 更新成否 (対象ユーザーが存在しなかった場合はfalse)
     */
    @Transactional
    public boolean updateAccount(UserRegisterForm form) {
        Optional<UserInfo> userOpt = userRepo.findById(form.getUserId());
        if (userOpt.isEmpty()) {
            return false;
        }

        UserInfo user = userOpt.get();
        
        // 1. 各項目の更新（パスワード以外）
        user.setUserName(form.getUserName());
        user.setPosition(Position.valueOf(form.getPosition()));
        
        // 💡 更新時も同様に、formの wageId からWageエンティティを取得してセット
        Wage wage = wageRepo.findById(form.getWageId())
            .orElseThrow(() -> new IllegalArgumentException("指定された賃金IDが存在しません: " + form.getWageId()));
        user.setWage(wage);
        
        if (form.getBirthDate() != null) {
            user.setBirthDate(java.sql.Date.valueOf(form.getBirthDate()));
        }
        
        user.setEmploymentInsurance(form.isEmploymentInsurance());
        user.setIsActive(form.getIsActive());

        // 2. パスワードの更新（画面で新しいパスワードが入力されている場合のみ暗号化して更新）
        if (form.getPassword() != null && !form.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(form.getPassword()));
        }

        userRepo.save(user);
        return true;
    }
}