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
        // 1. 1人目のユーザーを保存
        UserInfo user1 = new UserInfo();
        user1.setUserId("duplicate_id");
        user1.setUserName("ユーザー1");
        user1.setPassword("pwd1");
        user1.setIsActive(1);
        userInfoRepository.saveAndFlush(user1); // flushして即座にDBに反映！

        // 2. 同じID（duplicate_id）を持つ2人目のユーザーを作成
        UserInfo user2 = new UserInfo();
        user2.setUserId("duplicate_id"); // 💡 IDが重複！
        user2.setUserName("ユーザー2");
        user2.setPassword("pwd2");
        user2.setIsActive(1);

        // 3. 保存しようとした時、ちゃんと「DataIntegrityViolationException（一意制約エラー）」が発生するか検証！
        assertThrows(DataIntegrityViolationException.class, () -> {
            userInfoRepository.saveAndFlush(user2);
        }, "重複するIDで登録できた場合はテストが失敗しますぞい！");
    }
}