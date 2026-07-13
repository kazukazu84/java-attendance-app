package com.example.account.controller;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.DisabledException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {
	@GetMapping("/login")
    public String loginView(
            @RequestParam(value = "error", required = false) String error, 
            HttpServletRequest request, 
            Model model) {
        
        // 💡 エラーパラメータ（?error）が存在する場合のみ、メッセージ解析を実行
        if (error != null) {
            // Spring Securityがセッションの奥底に格納した「最後の例外」を奪取！
            Exception exception = (Exception) request.getSession()
                    .getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
            
            // デフォルトの冷徹なメッセージ
            String errorMessage = "ユーザーIDまたはパスワードが正しくありません。";
            
            // 💡 例外が「DisabledException（アカウント無効）」だった場合、優しいメッセージに書き換え！
            if (exception instanceof DisabledException) {
                errorMessage = "このアカウントは現在、休職または退職状態のためログインできません。";
            }
            
            // HTML（Thymeleaf）側へメッセージを届ける
            model.addAttribute("errorMsg", errorMessage);
        }
        
        return "kumeda/login";
    }
}