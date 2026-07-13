package com.example.account.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.account.entity.Position;
import com.example.account.entity.UserInfo;
import com.example.account.repository.UserInfoRepository;

@Service
public class AccountService {
    @Autowired private UserInfoRepository userRepo;
    @Autowired private PasswordEncoder passwordEncoder;

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
        
        // 1. 既存の基本パーツ（パスワードはしっかり暗号化）
        user.setUserId(id);
        user.setPassword(passwordEncoder.encode(rawPassword));
        
        // 2. 💡 順番通りにすべて詰め込む！
        user.setUserName(userName);
        user.setPosition(position); // 引数側がすでにEnum（Position）なのでそのままでOK！
        user.setWage(wage);
        user.setBirthDate(birthDate);
        user.setAttendanceStatus(attendanceStatus);
        user.setEmploymentInsurance(isEmploymentInsurance);
        user.setIsActive(isActive);
        
        // 3. DBへ保存
        userRepo.save(user);
    }
}