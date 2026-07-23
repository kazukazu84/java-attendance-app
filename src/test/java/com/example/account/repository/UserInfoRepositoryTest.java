package com.example.account.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;

import com.example.account.entity.Position;
import com.example.account.entity.UserInfo;
import com.example.account.entity.Wage;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // ⚡【ココを追加！】
@Transactional

public class UserInfoRepositoryTest {

	@Autowired
	private UserInfoRepository userInfoRepository;

	@Autowired
	private WageRepository wageRepository; // 🛡️ 新たに必要な賃金マスタ用リポジトリを注入！

	@Autowired
	private TestEntityManager entityManager; // 🛡️ JPA の低レイヤー操作用エンティティマネージャーを注入！

	// 💡 テストごとにクリーンな必須マスタ（Wage）をその場で作る共通メソッド
	private Wage createTestWage() {
		return wageRepository.findById(1)
				.orElseGet(() -> {
					Wage w = new Wage();
					w.setWageId(1);
					w.setWageValue(1000); // 適切な時給
					return wageRepository.saveAndFlush(w);
				});
	}

	@Test
	@DisplayName("💡 [正常系] ユーザー情報を正しく保存でき、IDで検索できること")
	void testSaveAndFindUser() {
		// 0. 新仕様の必須マスタを準備
		Wage testWage = createTestWage();

		// 1. テスト用のデータを作成（必須項目をすべてセット！）
		UserInfo user = new UserInfo();
		user.setUserId("test_user_999");
		user.setUserName("テスト太郎");
		user.setPassword("hashed_password_abc");
		user.setIsActive(1);

		// 🛡️ 新仕様で追加された必須項目たち
		user.setPosition(Position.USER);
		user.setWage(testWage);
		user.setEmploymentInsurance(true);
		user.setAttendanceStatus(0);

		// 2. データベースに保存（警告が出ないよう変数代入なしで直セーブ！）
		userInfoRepository.save(user);

		// 3. 保存されたデータが正しく取得できるか検証！
		Optional<UserInfo> foundUser = userInfoRepository.findById("test_user_999");
		assertTrue(foundUser.isPresent(), "ユーザーがDBに見つかりませんでしたぞい");
		assertEquals("テスト太郎", foundUser.get().getUserName());
		assertEquals(Position.USER, foundUser.get().getPosition());
	}

	@Test
    @DisplayName("💡 [異常系] 既に存在する同じユーザーIDで登録しようとした場合、一意制約違反（重複エラー）が発生すること")
    void testDuplicateUserIdThrowsException() {
        // 0. 新仕様の必須マスタを準備
        Wage testWage = createTestWage();

        // 1. 1人目のユーザーを作成
        UserInfo user1 = new UserInfo();
        user1.setUserId("duplicate_id");
        user1.setUserName("ユーザー1");
        user1.setPassword("pwd1");
        user1.setIsActive(1);
        user1.setPosition(Position.USER);
        user1.setWage(testWage);
        user1.setEmploymentInsurance(true);
        user1.setAttendanceStatus(0);
        user1.setBirthDate(java.sql.Date.valueOf("2000-01-01")); // 🎂 java.sql.Date 型でセット！
        
        userInfoRepository.saveAndFlush(user1); // 1人目をDBに確定保存！

        // 2. 同じID（duplicate_id）を持つ2人目のユーザーを作成
        UserInfo user2 = new UserInfo();
        user2.setUserId("duplicate_id"); // 💡 IDが重複！
        user2.setUserName("ユーザー2");
        user2.setPassword("pwd2");
        user2.setIsActive(1);
        user2.setPosition(Position.USER);
        user2.setWage(testWage);
        user2.setEmploymentInsurance(true);
        user2.setAttendanceStatus(0);
        user2.setBirthDate(java.sql.Date.valueOf("2000-01-01")); // 🎂 こちらも同じくセット！

        // 3. persist + flush で強制的・即座に重複インサートを実行させ、一意制約エラーを検証！
        assertThrows(Exception.class, () -> {
            entityManager.persist(user2);
            entityManager.flush();
        }, "重複するIDで登録できた場合はテストが失敗しますぞい！");
    }
}