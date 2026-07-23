package com.example.account.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.account.dto.UserRegisterForm;
import com.example.account.entity.Position;
import com.example.account.entity.UserInfo;
import com.example.account.entity.Wage;
import com.example.account.repository.WageRepository;
import com.example.account.service.AccountService;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerTest {

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

    // ==========================================
    // 1. 新規登録画面表示 (registerView)
    // ==========================================

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("新規登録画面表示：正常に登録画面が初期化されて表示されること")
    void registerView_shouldReturnRegisterViewWithDefaultForm() throws Exception {
        mockMvc.perform(get("/admin/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/admin/register"))
                .andExpect(model().attributeExists("userRegisterForm"))
                .andExpect(model().attribute("isNew", true));
    }

    // ==========================================
    // 2. 新規登録処理 (register)
    // ==========================================

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("新規登録：ユーザーIDに全角文字などの不正な文字が含まれる場合、バリデーションエラーとなり、登録画面に押し戻されること")
    void register_invalidUserId_shouldReturnRegisterViewWithErrors() throws Exception {
        mockMvc.perform(post("/admin/register")
                .with(csrf())
                .param("userId", "てすとID")
                .param("password", "password123")
                .param("userName", "テスト太郎")
                .param("position", "MANAGER")
                .param("wageId", "1")
                .param("birthDate", "1990-01-01")
                .param("employmentInsurance", "true")
                .param("isActive", "1")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(view().name("account/admin/register"))
                .andExpect(model().attributeHasFieldErrors("userRegisterForm", "userId"));

        verify(accountService, never()).registerAccount(any(UserRegisterForm.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("新規登録：パスワードが空の場合、エラーで登録画面に戻ること")
    void register_emptyPassword_shouldReturnRegisterView() throws Exception {
        mockMvc.perform(post("/admin/register")
                .with(csrf())
                .param("userId", "newuser")
                .param("userName", "テスト太郎")
                .param("password", "   ") // 空白のみ
                .param("position", "ADMIN")
                .param("isActive", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/admin/register"))
                .andExpect(model().attributeHasFieldErrors("userRegisterForm", "password"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("新規登録：ユーザーID重複などの不正な値（IllegalArgumentException）の場合、エラーメッセージ付きで画面に戻ること")
    void register_duplicateUser_shouldCatchExceptionAndReturnView() throws Exception {
        // Serviceが重複例外を投げるようモック化
        doThrow(new IllegalArgumentException("このユーザーIDは既に登録されています。"))
                .when(accountService).registerAccount(any());

        mockMvc.perform(post("/admin/register")
                .with(csrf())
                .param("userId", "duplicate_user")
                .param("userName", "テスト太郎")
                .param("password", "Password123!")
                .param("position", "ADMIN")
                .param("isActive", "1")
                .param("wageId", "1")
                .param("birthDate", "1990-01-01")
                .param("employmentInsurance", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/admin/register"))
                .andExpect(model().attributeHasFieldErrors("userRegisterForm", "userId"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("新規登録：正常な値が入力されている場合、バリデーションを通過し、一覧画面へリダイレクトされること")
    void register_validInput_shouldRedirectToUserManagement() throws Exception {
        mockMvc.perform(post("/admin/register")
                .with(csrf())
                .param("userId", "testUser123")
                .param("password", "password123")
                .param("userName", "テスト太郎")
                .param("position", "MANAGER")
                .param("wageId", "1")
                .param("birthDate", "1990-01-01")
                .param("employmentInsurance", "true")
                .param("isActive", "1")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/UserManagement"));

        verify(accountService, times(1)).registerAccount(any(UserRegisterForm.class));
    }

    // ==========================================
    // 3. 編集表示 (showEditForm)
    // ==========================================

    @Test
    @WithMockUser(username = "admin_user", roles = {"ADMIN"})
    @DisplayName("編集表示：自分自身のIDを指定した場合、拒否画面を表示すること")
    void showEditForm_selfEdit_shouldReturnDeniedView() throws Exception {
        mockMvc.perform(get("/admin/edit/admin_user"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/error-denied"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    @WithMockUser(username = "admin_user", roles = {"ADMIN"})
    @DisplayName("編集表示：存在しないユーザーIDを指定した場合、ユーザー一覧へリダイレクトすること")
    void showEditForm_notFound_shouldRedirectToUserManagement() throws Exception {
        when(accountService.findUserById("unknown")).thenReturn(Optional.empty());

        mockMvc.perform(get("/admin/edit/unknown"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/UserManagement"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    @WithMockUser(username = "admin_user", roles = {"ADMIN"})
    @DisplayName("編集表示：正常に他のユーザー情報を読み込んで編集画面が表示されること")
    void showEditForm_validUser_shouldReturnRegisterViewWithIsNewFalse() throws Exception {
        // 💡 関連エンティティのダミーを作成
    	Wage dummyWage = new Wage();
    	dummyWage.setWageId(1);
    	dummyWage.setWageValue(1000);

    	UserInfo dummyUser = new UserInfo();
    	dummyUser.setUserId("target_user");
    	dummyUser.setUserName("ターゲット太郎");
    	dummyUser.setPosition(Position.ADMIN);
    	dummyUser.setIsActive(1);

    	// 必須項目のセット
    	dummyUser.setWage(dummyWage);
    	dummyUser.setBirthDate(java.sql.Date.valueOf("1990-01-01"));
 
    	when(accountService.findUserById("target_user")).thenReturn(Optional.of(dummyUser));

        mockMvc.perform(get("/admin/edit/target_user"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/admin/register"))
                .andExpect(model().attribute("isNew", false))
                .andExpect(model().attributeExists("userRegisterForm"));
    }

    // ==========================================
    // 4. 更新処理 (updateAccount)
    // ==========================================

    @Test
    @WithMockUser(username = "admin_user", roles = {"ADMIN"})
    @DisplayName("更新処理：自分自身のIDを更新しようとした場合、拒否画面を表示すること")
    void updateAccount_selfUpdate_shouldReturnDeniedView() throws Exception {
        mockMvc.perform(post("/admin/update")
                .with(csrf())
                .param("userId", "admin_user")
                .param("userName", "更新名前"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/error-denied"));
    }

    @Test
    @WithMockUser(username = "admin_user", roles = {"ADMIN"})
    @DisplayName("更新処理：パスワードに空白文字のみが入力された場合、エラーで登録画面に戻ること")
    void updateAccount_blankPassword_shouldReturnRegisterViewWithErrors() throws Exception {
        mockMvc.perform(post("/admin/update")
                .with(csrf())
                .param("userId", "other_user")
                .param("userName", "テスト太郎")
                .param("password", "   ")) // 💡 更新時の空白パスワードチェック
                .andExpect(status().isOk())
                .andExpect(view().name("account/admin/register"))
                .andExpect(model().attributeHasFieldErrors("userRegisterForm", "password"))
                .andExpect(model().attribute("isNew", false));
    }

    @Test
    @WithMockUser(username = "admin_user", roles = {"ADMIN"})
    @DisplayName("更新処理：入力エラーがある場合、isNew=falseで登録画面に戻ること")
    void updateAccount_validationError_shouldReturnRegisterViewWithIsNewFalse() throws Exception {
        mockMvc.perform(post("/admin/update")
                .with(csrf())
                .param("userId", "other_user")
                .param("userName", "") // 必須違反
                .param("password", "Password123!"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/admin/register"))
                .andExpect(model().attribute("isNew", false));
    }

    @Test
    @WithMockUser(username = "admin_user", roles = {"ADMIN"})
    @DisplayName("更新処理：更新対象ユーザーが存在しない場合、一覧画面にリダイレクトすること")
    void updateAccount_userNotFoundInService_shouldRedirect() throws Exception {
        when(accountService.updateAccount(any())).thenReturn(false);

        mockMvc.perform(post("/admin/update")
                .with(csrf())
                .param("userId", "other_user")
                .param("userName", "テスト太郎")
                .param("password", "Password123!")
                .param("position", "ADMIN")
                .param("isActive", "1")
                .param("wageId", "1")
                .param("birthDate", "1990-01-01")
                .param("employmentInsurance", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/UserManagement"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    @WithMockUser(username = "admin_user", roles = {"ADMIN"})
    @DisplayName("更新処理：正常に更新された場合、一覧画面へリダイレクトすること")
    void updateAccount_success_shouldRedirectToUserManagement() throws Exception {
        when(accountService.updateAccount(any())).thenReturn(true);

        mockMvc.perform(post("/admin/update")
                .with(csrf())
                .param("userId", "other_user")
                .param("userName", "テスト太郎")
                .param("password", "Password123!")
                .param("position", "ADMIN")
                .param("isActive", "1")
                .param("wageId", "1")
                .param("birthDate", "1990-01-01")
                .param("employmentInsurance", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/UserManagement"))
                .andExpect(flash().attributeExists("successMessage"));
    }
}