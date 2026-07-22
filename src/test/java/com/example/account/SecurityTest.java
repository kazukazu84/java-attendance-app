package com.example.account;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc // 💡 ブラウザを使わずにリクエストを擬似送信する無敵のツール
public class SecurityTest {

    @Autowired
    private MockMvc mockMvc;

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
}