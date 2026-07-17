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

@Configuration @EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.formLogin(form -> form
            .loginPage("/login")
            .loginProcessingUrl("/authenticate")
            .successHandler((request, response, authentication) -> {
                // ここで直接、権限に応じた遷移先を変える！
                Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
            })
            .failureUrl("/login?error")
            .permitAll() 
        ).logout(logout -> logout.logoutSuccessUrl("/login?logout")
        		).authorizeHttpRequests(authz -> authz
        			    // ① 誰でもアクセスして良いエリア
        			    .requestMatchers("/login", "/css/**", "/js/**").permitAll()
        			    
        			    // ② 👑 管理者専用エリア（ここは ADMIN しか入れない堅牢な金庫室のまま）
        			    .requestMatchers("/admin/**").hasRole("ADMIN")
        			    
        			    // ③ 💼 一般ユーザーエリア（💡 ここを修正！）
        			    // USER または ADMIN のどちらかのロールを持っていれば、誰でも通れるようになります！
        			    .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
        			    
        			    // ④ それ以外のURLは、最低限「ログインしていること」
        			    .anyRequest().authenticated() 
 
        ).exceptionHandling(exceptionHandling -> exceptionHandling
            .accessDeniedPage("/error-denied") 
        );
        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
}