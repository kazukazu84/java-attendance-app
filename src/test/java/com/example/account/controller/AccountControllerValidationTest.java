package com.example.account.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.account.dto.UserRegisterForm;
import com.example.account.repository.WageRepository;
import com.example.account.service.AccountService;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService accountService;

    @MockitoBean
    private WageRepository wageRepo;

    @BeforeEach
    void setUp() {
        when(wageRepo.findAllByOrderByWageValueAsc()).thenReturn(new ArrayList<>());
    }
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("新規登録：ユーザーIDに全角文字などの不正な文字が含まれる場合、バリデーションエラーとなり、登録画面に押し戻されること")
    void register_invalidUserId_shouldReturnRegisterViewWithErrors() throws Exception {
        
        mockMvc.perform(post("/admin/register")
                .with(csrf()) // 👈 💡 これ！CSRFの盾を付与して403を回避するぞい！
                .param("userId", "てすとID")
                .param("password", "password123")
                .param("userName", "テスト太郎")
                .param("position", "MANAGER")
                .param("wageId", "1")
                .param("birthDate", "1990-01-01")
                .param("isEmploymentInsurance", "true")
                .param("isActive", "1")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                // 🛡️ 期待する挙動：エラーを検知して、登録画面に戻るべき
                .andExpect(status().isOk())
                .andExpect(view().name("account/admin/register"))
                .andExpect(model().attributeHasFieldErrors("userRegisterForm", "userId"));

        verify(accountService, never()).registerAccount(any(UserRegisterForm.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("新規登録：正常な値が入力されている場合、バリデーションを通過し、一覧画面へリダイレクトされること")
    void register_validInput_shouldRedirectToUserManagement() throws Exception {
        
        mockMvc.perform(post("/admin/register")
                .with(csrf()) // 👈 💡 こちらにもCSRFを付与！
                .param("userId", "testUser123")
                .param("password", "password123")
                .param("userName", "テスト太郎")
                .param("position", "MANAGER")
                .param("wageId", "1")
                .param("birthDate", "1990-01-01")
                .param("isEmploymentInsurance", "true")
                .param("isActive", "1")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                // 🛡️ 期待する挙動：正常に登録できて、一覧へリダイレクト
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/UserManagement"));

        verify(accountService, times(1)).registerAccount(any(UserRegisterForm.class));    }
}