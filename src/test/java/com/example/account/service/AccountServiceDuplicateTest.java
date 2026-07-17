package com.example.account.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.account.repository.UserInfoRepository;

@SpringBootTest
@Transactional // 👈 テスト実行後に自動でロールバックし、DBを汚さないおまじない
public class AccountServiceDuplicateTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserInfoRepository userInfoRepository;
/*
    @Test
    @DisplayName("重複登録防止テスト：既に存在するユーザーIDで登録を試みた場合、特定の例外が発生すること")
    void register_duplicateUserId_shouldThrowException() {
        
        // 1. 準備：テスト用のユーザーを1件、正常に登録しておく
        String duplicateId = "duplicateUser999";
        Calendar cal1 = Calendar.getInstance();
        cal1.set(1995, Calendar.MAY, 5);
        Date birthDate1 = cal1.getTime();
        
        accountService.registerAccount(
        		duplicateId,
                "password123",
                "既存ユーザー",
                Position.USER, // 👈 Enumクラスを直接渡す！
                1,
                birthDate1,      // 👈 java.util.Date型を渡す！
                0,
                true,
                1        );

        // 確実に対象IDがDBに保存されたことを確認
        assertTrue(userInfoRepository.existsById(duplicateId), "事前の登録に失敗しています");

        // 2. 実行 ＆ 検証
        // 同じID（duplicateId）で、2回目の登録を試みる
        // 🛡️ 期待する挙動：一意制約エラー（DataIntegrityViolationException）か、
        // もしくは「既にIDが存在します」というカスタム例外が発生して、処理を阻止すること！
        assertThrows(Exception.class, () -> {
            accountService.registerAccount(
            		duplicateId,
                    "password123",
                    "既存ユーザー",
                    Position.USER, // 👈 Enumクラスを直接渡す！
                    1,
                    birthDate1,      // 👈 java.util.Date型を渡す！
                    0,
                    true,
                    1
            );
        }, "重複IDでの登録時、例外が発生して処理が中断されなければなりません");
    }*/
}