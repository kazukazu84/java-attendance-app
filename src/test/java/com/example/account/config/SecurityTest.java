package com.example.account.config;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.account.entity.Position;
import com.example.account.entity.UserInfo;
import com.example.account.service.CustomUserDetails;

@SpringBootTest
@AutoConfigureMockMvc // 💡 ブラウザを使わずにリクエストを擬似送信する無敵のツール
public class SecurityTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    @DisplayName("💡 未ログインで管理画面に直打ちしたら、絶対に弾かれて302リダイレクト（ログインへ）されること")
    void testAnonymousCannotAccessAdmin() throws Exception {
        mockMvc.perform(get("/admin/UserManagement"))
               .andExpect(status().is3xxRedirection()); // 302（ログイン画面への強制送還）を検証！
    }

    @Test
    @WithMockUser(username = "user001", roles = "USER") // 💡 「一般ユーザー」の身分を偽装して突撃！
    @DisplayName("💡 一般ユーザーで管理者画面に直打ちしたら、403 Forbidden（アクセス拒否）になること")
    void testUserCannotAccessAdmin() throws Exception {
        mockMvc.perform(get("/admin/UserManagement"))
               .andExpect(status().isForbidden()); // 403 Forbidden（お前にその権限はない）を検証！
    }

    @Test
    @WithMockUser(username = "admin001", roles = "ADMIN") // 💡 「管理者」の身分で突撃！
    @DisplayName("💡 管理者ロールを持っていれば、正常に管理者画面にアクセスできること")
    void testAdminCanAccessAdmin() throws Exception {
        mockMvc.perform(get("/admin/UserManagement"))
               .andExpect(status().isOk()); // 200 OK（合格！）を検証！
    }
    
    @Test
    @WithMockUser(username = "admin001", roles = "ADMIN") // 💡 ログインユーザーは「admin001」
    @DisplayName("💡 自分自身（admin001）を無効化しようとしたら、error-denied画面に直行すること")
    void testDeactivateMyselfGoesToErrorPage() throws Exception {
        
        mockMvc.perform(
            // 💡 1. ターゲットとなるコントローラーのPOSTパスをここに指定！
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/admin/update")
                // 💡 2. フォームから送られてくるデータを再現して詰め込む！
                .param("userId", "admin001")     // 編集対象が「自分自身」
                .param("isActive", "0")          // かつ「無効化（退職）」にしようとしている
                .param("userName", "管理者A")     //（他に必要なフォームの値があれば適宜 param で追加するぞい！）
                
                // 🛡️ 3. Spring SecurityのCSRF保護を突破するためのお守り
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf())
        )
        // 💡 4. 【検証】システムはクラッシュせず、正常（200 OK）に画面を返すか？
        .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
        // 💡 5. 【検証】返ってきた画面名が、狙い通り「error-denied」になっているか！？
        .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.view().name("account/error-denied"));
    }
    
    @Test
    @WithMockUser(username = "admin001", roles = "ADMIN")
    @DisplayName("💡 CSRFトークンなしでPOST送ったら、403 Forbidden で弾かれること")
    void testPostWithoutCsrfShouldBeForbidden() throws Exception {
        mockMvc.perform(
            post("/admin/update")
                .param("userId", "admin001")
                .param("isActive", "1")
                // ⚡ あえて .with(csrf()) をつけない！
        )
        .andExpect(status().isForbidden()); // 403エラーで弾かれれば安全！
    }
    
    @Test
    @DisplayName("💡 無効化ユーザー（isActive = 0）でログインを試みると DisabledException が発生して /login?error へリダイレクトされること")
    void testDisabledUserLoginFailsWithDisabledException() throws Exception {
        
        // 1. このシステムの UserInfo エンティティを作成 (isActive = 0: 無効アカウント)
        UserInfo disabledUserInfo = new UserInfo();
        disabledUserInfo.setUserId("disabled_user");
        disabledUserInfo.setPassword("$2a$10$e.g.HashedPassword..."); // BCrypt等の形式
        disabledUserInfo.setPosition(Position.USER);
        disabledUserInfo.setIsActive(0); // 💡 ここでアカウント無効（enabled = false に変換される）を設定！

        // 2. 自作の CustomUserDetails に渡して UserDetailsService から返させる
        CustomUserDetails customUserDetails = new CustomUserDetails(disabledUserInfo);
        org.mockito.Mockito.when(userDetailsService.loadUserByUsername("disabled_user"))
               .thenReturn(customUserDetails);

        // 3. SecurityConfig の loginProcessingUrl ("/authenticate") へログイン実行
        mockMvc.perform(
            post("/authenticate") // 💡 設定通り /authenticate でOK！
                .param("username", "disabled_user")
                .param("password", "password123")
                .with(csrf())
        )
        // ① 302 リダイレクトされること
        .andExpect(status().is3xxRedirection())
        
        // ② SecurityConfig の failureUrl ("/login?error") へ遷移すること
        .andExpect(redirectedUrl("/login?error"))
        
        // ③ セッションに DisabledException（無効化例外）が記録されていることを検証
        .andExpect(result -> {
            Object lastException = result.getRequest()
                .getSession()
                .getAttribute("SPRING_SECURITY_LAST_EXCEPTION");

            org.junit.jupiter.api.Assertions.assertNotNull(
                lastException, 
                "セッションに Spring Security の例外がセットされていません"
            );

            org.junit.jupiter.api.Assertions.assertInstanceOf(
                org.springframework.security.authentication.DisabledException.class, 
                lastException,
                "失敗理由が DisabledException ではありません（CustomUserDetails の isEnabled() が false になっているか確認してください）"
            );
        });
    }
    
    @Test
    @WithMockUser(username = "user001", roles = "USER") // 💡 一般ユーザーでログイン
    @DisplayName("💡 一般ユーザーが管理者用POST処理（/admin/update）を直接叩いたら 403 Forbidden で弾かれること")
    void testUserCannotPostToAdminUpdate() throws Exception {
        mockMvc.perform(
            post("/admin/update")
                .param("userId", "user002")
                .param("isActive", "0")
                .with(csrf())
        )
        .andExpect(status().isForbidden()); // 403 で返り討ち！
    }
}