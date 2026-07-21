package com.example.account.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.account.dto.UserRegisterForm;
import com.example.account.repository.UserInfoRepository;

@SpringBootTest
@Transactional // 👈 テスト実行後に自動でロールバックし、DBを汚さないおまじない
public class AccountServiceDuplicateTest {

	@Autowired
	private AccountService accountService;

	@Autowired
	private UserInfoRepository userInfoRepository;

	@Test
	@DisplayName("重複登録防止テスト：既に存在するユーザーIDで登録を試みた場合、特定の例外が発生すること")
	void register_duplicateUserId_shouldThrowException() {

		String duplicateId = "duplicateUser999";

		// ----------------------------------------------------
		// 1. 準備：1件目のユーザー登録（Formを作成して渡す）
		// ----------------------------------------------------
		UserRegisterForm form1 = new UserRegisterForm();
		form1.setUserId(duplicateId);
		form1.setPassword("password123");
		form1.setUserName("既存ユーザー");
		form1.setPosition("USER"); // 💡 サービス内部で値からEnumに変換されるので文字列でOK！
		form1.setWageId(1);
		form1.setBirthDate(java.time.LocalDate.of(1995, 5, 5)); // 💡 サービスがLocalDateで受けるようになったのでDate変換も不要に！
		form1.setEmploymentInsurance(true);
		form1.setIsActive(1);

		// 新しいメソッド形式で1件目を登録！
		accountService.registerAccount(form1);

		// 確実に対象IDがDBに保存されたことを確認
		assertTrue(userInfoRepository.existsById(duplicateId), "事前の登録に失敗しています");

		// ----------------------------------------------------
		// 2. 実行 ＆ 検証（同じIDで2件目のFormを作って突撃）
		// ----------------------------------------------------
		UserRegisterForm form2 = new UserRegisterForm();
		form2.setUserId(duplicateId); // 👈 わざと全く同じIDを指定！
		form2.setPassword("newPassword123");
		form2.setUserName("重複させたい新規ユーザー");
		form2.setPosition("USER");
		form2.setWageId(1);
		form2.setBirthDate(java.time.LocalDate.of(2000, 1, 1));
		form2.setEmploymentInsurance(true);
		form2.setIsActive(1);

		// 🛡️ 重複IDで実行した時、例外が発生して処理が中断されるかを検証！
		assertThrows(Exception.class, () -> {
			accountService.registerAccount(form2); // 👈 新しいメソッド形式で呼び出し！
		}, "重複IDでの登録時、例外が発生して処理が中断されなければなりません");
	}
}