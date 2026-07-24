package com.example.account.config;

import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

//import com.example.account.handler.CustomLoginSuccessHandler;
//import com.example.account.handler.CustomLogoutSuccessHandler;

@Configuration 
@EnableWebSecurity
public class SecurityConfig {
	//ログインのログ・メッセージを機能させる場合、コメントを外す。
	//private final CustomLoginSuccessHandler customLoginSuccessHandler;
	//private final CustomLogoutSuccessHandler customLogoutSuccessHandler;

	//public SecurityConfig(CustomLoginSuccessHandler customLoginSuccessHandler,
	//                           CustomLogoutSuccessHandler customLogoutSuccessHandler) {
	//    this.customLoginSuccessHandler = customLoginSuccessHandler;
	//    this.customLogoutSuccessHandler = customLogoutSuccessHandler;
	//}
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.formLogin(form -> form
				.loginPage("/login")
				.loginProcessingUrl("/authenticate")
				//ログインのログ・メッセージを機能させる場合、コメントを外す。
				// 💡 ログイン成功時のハンドラーを指定
				//   .successHandler(customLoginSuccessHandler)
				.successHandler((request, response, authentication) -> {
					//ログインのログ・メッセージを機能させる場合、↑コメントはコメントアウト。


					// 💡 ログインしたユーザーの持つ権限（ロール）をセットとして取得
					Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

					// 🛡️ ロールに応じて遷移先を切り替える処理を完遂させる！
					if (roles.contains("ROLE_ADMIN")) {
						response.sendRedirect("/admin/main");// 👑 管理者は管理画面（登録画面など）へ
					}else {
						response.sendRedirect("/user/attendance"); // 💼 一般ユーザーはマイページ/トップへ（※実際のパスに合わせて調整してください）
					}
				})
				.failureUrl("/login?error")
				.permitAll()            
				).logout(logout -> logout
						.logoutSuccessUrl("/login?logout")
						//ログインのログ・メッセージを機能させる場合、↑コメントはコメントアウト。
						//ログインのログ・メッセージを機能させる場合、コメントを外す。
						// 💡 ログアウト成功時のハンドラーを指定（リダイレクト処理もハンドラー内で行います）
						//   .logoutSuccessHandler(customLogoutSuccessHandler)
						//   .permitAll()
						).authorizeHttpRequests(authz -> authz
								// ① 誰でもアクセスして良いエリア
								.requestMatchers("/login", "/css/**", "/js/**").permitAll()

								// ② 👑 管理者専用エリア
								.requestMatchers("/admin/**").hasRole("ADMIN")

								// ③ 💼 一般ユーザーエリア
								.requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")

								// ④ それ以外のURLはログイン必須

								.anyRequest().authenticated() 
								).exceptionHandling(exceptionHandling -> exceptionHandling
										.accessDeniedPage("/error-denied") 
										);
		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() { 
		return new BCryptPasswordEncoder(); 
	}

}