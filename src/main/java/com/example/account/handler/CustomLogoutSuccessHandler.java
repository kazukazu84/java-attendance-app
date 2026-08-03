package com.example.account.handler;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.main.service.LogService;

@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

	private final LogService logService;

	public CustomLogoutSuccessHandler(LogService logService) {
		this.logService = logService;
	}

	@Override
	public void onLogoutSuccess(HttpServletRequest request, 
			HttpServletResponse response, 
			Authentication authentication) throws IOException, ServletException {

		if (authentication != null) {
			String userName = authentication.getName();
			// ★メッセージID: 11（"{user_name}さんがログアウトしました"）でログ登録
			//DataInitializerで定義した番号を使う
			logService.saveLog(11, userName);
		}

		// ログアウト後の遷移先へリダイレクト
		response.sendRedirect("/login?logout");
	}
}