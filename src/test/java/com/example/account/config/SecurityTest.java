package com.example.account.config;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

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
    @DisplayName("💡 無効化ユーザー（休職/退職）でログインを試みると DisabledException が発生して /login?error へリダイレクトされること")
    void testDisabledUserLoginFailsWithDisabledException() throws Exception {
        
        // 1. 無効化された（enabled = false）ユーザーを Mock で準備
        // ※ もし H2 などのテスト用 DB にデータがある場合は UserDetailsService の Mock は不要です
        org.springframework.security.core.userdetails.UserDetails disabledUser = 
            org.springframework.security.core.userdetails.User.builder()
                .username("disabled_user")
                .password("{noop}password123") // 平文比較用の {noop} プレフィックス
                .roles("USER")
                .disabled(true) // 💡 明示的にアカウントを無効化（enabled = false）
                .build();

        // UserDetailsService が呼ばれたら無効化ユーザーを返すように設定
        org.mockito.Mockito.when(userDetailsService.loadUserByUsername("disabled_user"))
               .thenReturn(disabledUser);

        // 2. ログイン実行と検証
        mockMvc.perform(
            post("/authenticate")
                .param("username", "disabled_user")
                .param("password", "password123")
                .with(csrf())
        )
        // ① 302 リダイレクトされること
        .andExpect(status().is3xxRedirection())
        
        // ② エラーパラメータ付きのログイン画面へ遷移すること
        .andExpect(redirectedUrl("/login?error"))
        
        // ③ セッションに DisabledException（無効化例外）が記録されていることを厳密に検証！
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
                "失敗理由が DisabledException ではありません（単なるID/パスワード間違いの可能性があります）"
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