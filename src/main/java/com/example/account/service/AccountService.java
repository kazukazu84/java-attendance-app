package com.example.account.service;

import java.util.List;
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
import com.example.account.repository.WageRepository;

@Service
public class AccountService {
    @Autowired private UserInfoRepository userRepo;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private WageRepository wageRepo;
    
    /**
     * IDによるユーザー検索
     */
    public Optional<UserInfo> findUserById(String id) {
        return userRepo.findById(id);
    }

    /**
     * 💡 ユーザーIDが既に存在するか確認する
     */
    public boolean existsByUserId(String id) {
        return userRepo.existsById(id);
    }

    /**
     * 💡 新規アカウント登録（UserRegisterFormを直接受け取るように修正）
     */
    @Transactional
    public void registerAccount(UserRegisterForm form) {
        if (existsByUserId(form.getUserId())) {
            throw new IllegalArgumentException("このユーザーIDは既に登録されています。");
        }
    	UserInfo user = new UserInfo();
        
        user.setUserId(form.getUserId());
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setUserName(form.getUserName());
        user.setPosition(Position.valueOf(form.getPosition()));
        
        Wage wage = wageRepo.findById(form.getWageId())
            .orElseThrow(() -> new IllegalArgumentException("指定された賃金IDが存在しません: " + form.getWageId()));
        user.setWage(wage);
        
        if (form.getBirthDate() != null) {
            user.setBirthDate(java.sql.Date.valueOf(form.getBirthDate()));
        }
        
        user.setAttendanceStatus(0); // 勤怠状況の初期値
        user.setEmploymentInsurance(form.isEmploymentInsurance());
        user.setIsActive(form.getIsActive());
        
        userRepo.save(user);
    }

    /**
     * 既存アカウント更新
     * @return 更新成否
     */
    @Transactional
    public boolean updateAccount(UserRegisterForm form) {
        Optional<UserInfo> userOpt = userRepo.findById(form.getUserId());
        if (userOpt.isEmpty()) {
            return false;
        }
        
        UserInfo user = userOpt.get();
        
        user.setUserName(form.getUserName());
        user.setPosition(Position.valueOf(form.getPosition()));
        
        Wage wage = wageRepo.findById(form.getWageId())
            .orElseThrow(() -> new IllegalArgumentException("指定された賃金IDが存在しません: " + form.getWageId()));
        user.setWage(wage);
        
        //if (form.getBirthDate() != null) {
            user.setBirthDate(java.sql.Date.valueOf(form.getBirthDate()));
        //}
        
        user.setEmploymentInsurance(form.isEmploymentInsurance());
        user.setIsActive(form.getIsActive());

        // 💡 空文字、または初期表示の「＊＊＊＊＊＊＊＊」のままの場合はパスワード更新をスキップする
        if (form.getPassword() != null && !form.getPassword().trim().isEmpty() && !form.getPassword().equals("＊＊＊＊＊＊＊＊")) {
            user.setPassword(passwordEncoder.encode(form.getPassword()));
        }

        userRepo.save(user);
        return true;
    }

    @Transactional
    public void deactivateUsers(List<String> userIds) {
        List<UserInfo> users = userRepo.findAllById(userIds);
        for (UserInfo user : users) {
            user.setIsActive(0);
        }
        userRepo.saveAll(users);
    }

    public List<UserInfo> searchUsers(String keyword, String type) {
        if ("id".equals(type)) {
            return userRepo.findByUserIdContaining(keyword);
        } else if ("name".equals(type)) {
            return userRepo.findByUserNameContaining(keyword);
        }
        return userRepo.findAll();
    }
}