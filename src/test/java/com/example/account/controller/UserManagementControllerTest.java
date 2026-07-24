package com.example.account.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.account.repository.UserInfoRepository;
import com.example.account.service.AccountService;

@SpringBootTest
@AutoConfigureMockMvc
public class UserManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService accountService;

    @MockitoBean
    private UserInfoRepository userInfoRepository;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("ユーザー管理一覧表示：全ユーザーがモデルに設定されて一覧画面が返ること")
    void usermanagementView_shouldReturnUserListView() throws Exception {
        when(userInfoRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/admin/UserManagement"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/admin/UserManagement"))
                .andExpect(model().attributeExists("users"));

        verify(accountService, times(1)).searchUsers("", "");
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("ユーザー検索：キーワード指定でServiceが呼ばれ、検索結果がセットされること")
    void search_shouldReturnSearchResults() throws Exception {
        when(accountService.searchUsers("テスト", "name")).thenReturn(List.of());

        mockMvc.perform(get("/admin/users/search")
                .param("keyword", "テスト")
                .param("type", "name"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/admin/UserManagement"))
                .andExpect(model().attributeExists("users"));

        verify(accountService, times(1)).searchUsers("テスト", "name");
    }

    @Test
    @WithMockUser(username = "adminUser", roles = {"ADMIN"})
    @DisplayName("一括無効化【セキュリティガード】：リストに自分自身が含まれる場合、拒否画面を表示し無効化処理を実行しないこと")
    void batchDeactivate_containsSelf_shouldReturnErrorDenied() throws Exception {
        // ログインユーザー名("adminUser")を含んだリストを送信
        mockMvc.perform(post("/admin/users/batch-deactivate")
                .with(csrf())
                .param("userIds", "user01", "adminUser", "user02"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/error-denied"));

        // 🛡️ Serviceの無効化メソッドは絶対に呼ばれていないことを検証！
        verify(accountService, never()).deactivateUsers(any());
    }

    @Test
    @WithMockUser(username = "adminUser", roles = {"ADMIN"})
    @DisplayName("一括無効化：他ユーザーのみのリストの場合、無効化処理が呼ばれ一覧へリダイレクトすること")
    void batchDeactivate_validUsers_shouldRedirectToUserManagement() throws Exception {
        mockMvc.perform(post("/admin/users/batch-deactivate")
                .with(csrf())
                .param("userIds", "user01", "user02"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/UserManagement"));

        verify(accountService, times(1)).deactivateUsers(List.of("user01", "user02"));
    }
}