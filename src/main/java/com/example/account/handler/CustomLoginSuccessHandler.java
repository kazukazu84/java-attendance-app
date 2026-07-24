package com.example.account.handler;

import java.io.IOException;
import java.util.Set;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.main.service.LogService; // ログ保存用サービス

@Component
public class CustomLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final LogService logService;

    public CustomLoginSuccessHandler(LogService logService) {
        this.logService = logService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
        // ログインしたユーザー名（ID）を取得
        String userName = authentication.getName();

        // ★メッセージID: 10（"{user_name}さんがログインしました"）でログ登録
        //DataInitializerで定義した番号を使う
        logService.saveLog(10, userName);

        // 💡 既存のロール別リダイレクト処理
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        
        if (roles.contains("ROLE_ADMIN")) {
            response.sendRedirect("/admin/main"); // 👑 管理者は管理画面へ
        } else {
            response.sendRedirect("/user/attendance"); // 💼 一般ユーザーはマイページへ
        }
    }
}