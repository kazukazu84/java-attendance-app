package com.example.account.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;

import com.example.account.entity.UserInfo;

@DataJpaTest // 💡 JPA関連のコンポーネントだけをスライスして起動する超軽量アノテーション！
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 💡 本番（PostgreSQL）の設定をそのままテストで使用する場合に指定！
public class UserInfoRepositoryTest {

    @Autowired
    private UserInfoRepository userInfoRepository;
    
    @Autowired
    private com.example.account.repository.WageRepository wageRepository; // 👈 賃金リポジトリも追加

    @Test
    @DisplayName("💡 [正常系] ユーザー情報を正しく保存でき、IDで検索できること")
    void testSaveAndFindUser() {
        // 1. テスト用のデータを作成
        UserInfo user = new UserInfo();
        user.setUserId("test_user_999");
        user.setUserName("テスト太郎");
        user.setPassword("hashed_password_abc");
        user.setIsActive(1);

        // 2. データベースに保存
        UserInfo savedUser = userInfoRepository.save(user);

        // 3. 保存されたデータが正しく取得できるか検証！
        Optional<UserInfo> foundUser = userInfoRepository.findById("test_user_999");
        assertTrue(foundUser.isPresent(), "ユーザーがDBに見つかりませんでしたぞい");
        assertEquals("テスト太郎", foundUser.get().getUserName());
    }

    @Test
    @DisplayName("💡 [異常系] 既に存在する同じユーザーIDで登録しようとした場合、一意制約違反（重複エラー）が発生すること")
    void testDuplicateUserIdThrowsException() {
        
        // 💡 0. 必須項目である「Wage」のマスタデータが存在するか確認（なければテスト内で作るか、1番を取得）
        com.example.account.entity.Wage testWage = wageRepository.findById(1)
                .orElseGet(() -> {
                    // もしテストDBが空なら、その場で一時的に作成する
                    com.example.account.entity.Wage w = new com.example.account.entity.Wage();
                    w.setWageId(1);
                    w.setWageValue(1000); // 適切な初期値
                    return wageRepository.saveAndFlush(w);
                });

        // 1. 1人目のユーザーを保存
        UserInfo user1 = new UserInfo();
        user1.setUserId("duplicate_id");
        user1.setUserName("ユーザー1");
        user1.setPassword("pwd1");
        user1.setIsActive(1);
        
        // 🛡️ 新たに追加された必須項目をしっかり埋める！
        user1.setPosition(com.example.account.entity.Position.USER);
        user1.setWage(testWage);
        user1.setEmploymentInsurance(true);
        user1.setAttendanceStatus(0);

        userInfoRepository.saveAndFlush(user1); 

        // 2. 同じIDを持つ2人目のユーザーを作成
        UserInfo user2 = new UserInfo();
        user2.setUserId("duplicate_id"); 
        user2.setUserName("ユーザー2");
        user2.setPassword("pwd2");
        user2.setIsActive(1);
        
        // 🛡️ 2人目も同様に必須項目を埋める！
        user2.setPosition(com.example.account.entity.Position.USER);
        user2.setWage(testWage);
        user2.setEmploymentInsurance(true);
        user2.setAttendanceStatus(0);

        // 3. これで他の制約をすべてクリアし、純粋に「重複エラー」だけを発生させる！
        assertThrows(DataIntegrityViolationException.class, () -> {
            userInfoRepository.saveAndFlush(user2);
        }, "重複するIDで登録できた場合はテストが失敗しますぞい！");
    }
}