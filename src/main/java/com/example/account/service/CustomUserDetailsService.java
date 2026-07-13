package com.example.account.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.account.entity.UserInfo;
import com.example.account.repository.UserInfoRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserInfoRepository userInfoRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // ① DBから自作のUserInfoエンティティを検索
        UserInfo userInfo = userInfoRepository.findById(username)
            .orElseThrow(() -> new UsernameNotFoundException("ユーザーが見つかりません"));

        // ② 重要：Spring標準の箱ではなく、自作した「CustomUserDetails」へUserInfoをそのまま渡して返す！
        return new CustomUserDetails(userInfo);
    }
}