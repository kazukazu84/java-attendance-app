package com.example.account.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("ログイン画面：DisabledException発生時、休職/退職用メッセージがモデルに設定されること")
    void loginView_disabledException_shouldSetCustomErrorMessage() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("SPRING_SECURITY_LAST_EXCEPTION", new DisabledException("Account disabled"));

        mockMvc.perform(get("/login")
                .param("error", "true")
                .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("account/login"))
                .andExpect(model().attribute("errorMsg", "このアカウントは現在、休職または退職状態のためログインできません。"));
    }

    @Test
    @DisplayName("ログイン画面：通常の認証エラー時、デフォルトのエラーメッセージがモデルに設定されること")
    void loginView_generalException_shouldSetDefaultErrorMessage() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("SPRING_SECURITY_LAST_EXCEPTION", new BadCredentialsException("Bad credentials"));

        mockMvc.perform(get("/login")
                .param("error", "true")
                .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("account/login"))
                .andExpect(model().attribute("errorMsg", "ユーザーIDまたはパスワードが正しくありません。"));
    }
}