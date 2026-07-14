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
import com.example.account.repository.UserInfoRepository;

@Service
public class AccountService {
    @Autowired private UserInfoRepository userRepo;
    @Autowired private PasswordEncoder passwordEncoder;

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
        int wage, 
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
        user.setWage(wage);
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
        user.setWage(form.getWage());
        
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