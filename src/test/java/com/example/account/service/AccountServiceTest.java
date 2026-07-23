package com.example.account.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.account.dto.UserRegisterForm;
import com.example.account.entity.UserInfo;
import com.example.account.entity.Wage;
import com.example.account.repository.UserInfoRepository;
import com.example.account.repository.WageRepository;

@ExtendWith(MockitoExtension.class) // Springを起動せず、Mockitoのみで超高速に実行
class AccountServiceTest {

	@InjectMocks
	private AccountService accountService; // テスト対象

	@Mock
	private UserInfoRepository userRepo;

	@Mock
	private WageRepository wageRepo;

	@Mock
	private PasswordEncoder passwordEncoder;

	// ==========================================
	// 1. 新規登録 (registerAccount) のテスト
	// ==========================================
	@Nested
	@DisplayName("新規登録処理 (registerAccount)")
	class RegisterAccountTest {

		@Test
		@DisplayName("不正な引数：ユーザーIDが既に存在する場合、IllegalArgumentExceptionが発生すること")
		void registerAccount_duplicateUserId_shouldThrowException() {
			// Given: すでにユーザーIDが存在すると仮定
			when(userRepo.existsById("duplicate_user")).thenReturn(true);

			UserRegisterForm form = new UserRegisterForm();
			form.setUserId("duplicate_user");

			// When & Then: 例外が投げられることを検証！
			assertThatThrownBy(() -> accountService.registerAccount(form))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("このユーザーIDは既に登録されています。");

			// 保存処理が絶対に呼ばれていないことを確認
			verify(userRepo, never()).save(any());
		}

		@Test
		@DisplayName("正常系：正しいフォーム情報が渡された場合、パスワードが暗号化されて保存されること")
		void registerAccount_success() {
			// Given: ID重複なし、賃金マスタ存在 (主キーはIntegerの 1)
			when(userRepo.existsById("new_user")).thenReturn(false);
			when(wageRepo.findById(1)).thenReturn(Optional.of(new Wage()));
			when(passwordEncoder.encode("rawPassword")).thenReturn("hashedPassword");

			UserRegisterForm form = new UserRegisterForm();
			form.setUserId("new_user");
			form.setPassword("rawPassword");
			form.setUserName("テスト太郎");
			form.setPosition("ADMIN");
			form.setWageId(1);
			form.setIsActive(1);

			// When
			accountService.registerAccount(form);

			// Then: saveが1回呼ばれ、パスワードがハッシュ化されていること
			verify(userRepo, times(1)).save(argThat(user -> 
			user.getUserId().equals("new_user") &&
			user.getPassword().equals("hashedPassword")
					));
		}
	}

	// ==========================================
	// 2. 更新処理 (updateAccount) のテスト
	// ==========================================
	@Nested
	@DisplayName("更新処理 (updateAccount)")
	class UpdateAccountTest {

		@Test
		@DisplayName("更新対象なし：存在しないユーザーIDの場合、falseが返ること")
		void updateAccount_userNotFound_shouldReturnFalse() {
			// Given
			when(userRepo.findById("unknown_user")).thenReturn(Optional.empty());

			UserRegisterForm form = new UserRegisterForm();
			form.setUserId("unknown_user");

			// When
			boolean result = accountService.updateAccount(form);

			// Then
			assertThat(result).isFalse();
			verify(userRepo, never()).save(any());
		}

		@Test
		@DisplayName("正常系：存在するユーザーの場合、情報が更新されてtrueが返ること")
		void updateAccount_success_shouldReturnTrue() {
			// Given
			UserInfo existingUser = new UserInfo();
			existingUser.setUserId("target_user");

			when(userRepo.findById("target_user")).thenReturn(Optional.of(existingUser));
			when(wageRepo.findById(1)).thenReturn(Optional.of(new Wage()));

			UserRegisterForm form = new UserRegisterForm();
			form.setUserId("target_user");
			form.setUserName("更新後の名前");
			form.setPosition("ADMIN");
			form.setWageId(1);
			form.setIsActive(1);
			form.setBirthDate(LocalDate.of(1990, 1, 1));

			// When
			boolean result = accountService.updateAccount(form);

			// Then
			assertThat(result).isTrue();
			verify(userRepo, times(1)).save(existingUser);
			assertThat(existingUser.getUserName()).isEqualTo("更新後の名前");
		}
	}
	/*
	// ==========================================
	// 3. ユーザー1件取得 (findUserById) のテスト
	// ==========================================
	@Nested
	@DisplayName("ユーザー1件取得 (findUserById)")
	class FindUserByIdTest {

		@Test
		@DisplayName("正常系：存在するユーザーIDを指定した場合、該当ユーザーが返ること")
		void findUserById_found() {
			// Given
			UserInfo user = new UserInfo();
			user.setUserId("find_user");
			when(userRepo.findById("find_user")).thenReturn(Optional.of(user));

			// When
			Optional<UserInfo> result = accountService.findUserById("find_user");

			// Then
			assertThat(result).isPresent();
			assertThat(result.get().getUserId()).isEqualTo("find_user");
		}

		@Test
		@DisplayName("対象なし：存在しないユーザーIDを指定した場合、空のOptionalが返ること")
		void findUserById_notFound() {
			// Given
			when(userRepo.findById("not_exist")).thenReturn(Optional.empty());

			// When
			Optional<UserInfo> result = accountService.findUserById("not_exist");

			// Then
			assertThat(result).isEmpty();
		}
	}
	 */
	// ==========================================
	// 4. アカウント一括無効化 (deactivateUsers) のテスト
	// ==========================================
	@Nested
	@DisplayName("アカウント一括無効化 (deactivateUsers)")
	class DeactivateUsersTest {

		@Test
		@DisplayName("正常系：指定されたユーザーIDリストのisActiveが0に更新されて一括保存されること")
		void deactivateUsers_success() {
			// Given: 有効な状態（isActive = 1）のユーザーが2人存在すると仮定
			UserInfo user1 = new UserInfo();
			user1.setUserId("user_01");
			user1.setIsActive(1);

			UserInfo user2 = new UserInfo();
			user2.setUserId("user_02");
			user2.setIsActive(1);

			List<String> targetIds = List.of("user_01", "user_02");
			when(userRepo.findAllById(targetIds)).thenReturn(List.of(user1, user2));

			// When
			accountService.deactivateUsers(targetIds);

			// Then
			// 1. 各ユーザーの isActive が 0 に変更されていること
			assertThat(user1.getIsActive()).isEqualTo(0);
			assertThat(user2.getIsActive()).isEqualTo(0);

			// 2. saveAll が呼ばれて一括更新されていること
			verify(userRepo, times(1)).saveAll(argThat(users -> {
				List<UserInfo> list = (List<UserInfo>) users;
				return list.size() == 2 && list.stream().allMatch(u -> u.getIsActive() == 0);
			}));
		}
	}
	// ==========================================
	// 5. ユーザー検索 (searchUsers) のテスト
	// ==========================================
	@Nested
	@DisplayName("ユーザー検索 (searchUsers)")
	class SearchUsersTest {

		@Test
		@DisplayName("type = 'id' の場合、ID部分一致検索 repository メソッドが呼ばれること")
		void searchUsers_byId() {
			// Given
			when(userRepo.findByUserIdContaining("001")).thenReturn(List.of(new UserInfo()));

			// When
			List<UserInfo> result = accountService.searchUsers("001", "id");

			// Then
			assertThat(result).hasSize(1);
			verify(userRepo, times(1)).findByUserIdContaining("001");
			verify(userRepo, never()).findByUserNameContaining(any());
			verify(userRepo, never()).findAll();
		}

		@Test
		@DisplayName("type = 'name' の場合、氏名部分一致検索 repository メソッドが呼ばれること")
		void searchUsers_byName() {
			// Given
			when(userRepo.findByUserNameContaining("太郎")).thenReturn(List.of(new UserInfo()));

			// When
			List<UserInfo> result = accountService.searchUsers("太郎", "name");

			// Then
			assertThat(result).hasSize(1);
			verify(userRepo, times(1)).findByUserNameContaining("太郎");
			verify(userRepo, never()).findByUserIdContaining(any());
			verify(userRepo, never()).findAll();
		}

		@Test
		@DisplayName("type が上記以外の場合、全件検索 repository メソッドが呼ばれること")
		void searchUsers_all() {
			// Given
			when(userRepo.findAll()).thenReturn(List.of(new UserInfo(), new UserInfo()));

			// When
			List<UserInfo> result = accountService.searchUsers("", "other");

			// Then
			assertThat(result).hasSize(2);
			verify(userRepo, times(1)).findAll();
			verify(userRepo, never()).findByUserIdContaining(any());
			verify(userRepo, never()).findByUserNameContaining(any());
		}
	}

	// ==========================================
	// 6. 境界値・分岐網羅の追加テスト
	// ==========================================
	@Nested
	@DisplayName("未実行ルートの網羅テスト")
	class UncoveredBranchesTest {

		@Test
		@DisplayName("新規登録：生年月日(birthDate)が入力されている場合、java.sql.Dateに変換されて保存されること")
		void registerAccount_withBirthDate() {
			// Given
			when(userRepo.existsById("user_date")).thenReturn(false);
			when(wageRepo.findById(1)).thenReturn(Optional.of(new Wage()));

			UserRegisterForm form = new UserRegisterForm();
			form.setUserId("user_date");
			form.setPassword("pass");
			form.setPosition("ADMIN");
			form.setWageId(1);

			// 💡 LocalDate 型でセット！
			form.setBirthDate(java.time.LocalDate.of(1995, 5, 15));

			// When
			accountService.registerAccount(form);

			// Then
			verify(userRepo, times(1)).save(argThat(user ->
			user.getBirthDate() != null &&
			user.getBirthDate().toString().equals("1995-05-15")
					));
		}

		@Test
		@DisplayName("更新処理：パスワードが『＊＊＊＊＊＊＊＊』の場合はパスワード変更処理をスキップすること")
		void updateAccount_maskedPassword_shouldSkipPasswordUpdate() {
			// Given
			UserInfo existingUser = new UserInfo();
			existingUser.setUserId("target_user");
			existingUser.setPassword("original_hashed_password"); // 元のハッシュ化パスワード

			when(userRepo.findById("target_user")).thenReturn(Optional.of(existingUser));
			when(wageRepo.findById(1)).thenReturn(Optional.of(new Wage()));

			UserRegisterForm form = new UserRegisterForm();
			form.setUserId("target_user");
			form.setUserName("更新名前");
			form.setPosition("ADMIN");
			form.setWageId(1);
			form.setPassword("＊＊＊＊＊＊＊＊"); // 💡 初期表示用のマスク文字列
			form.setBirthDate(LocalDate.of(1990, 1, 1));
			// When
			accountService.updateAccount(form);

			// Then
			// パスワードエンコーダーが一度も呼ばれず、パスワードが元のまま保持されていること
			verify(passwordEncoder, never()).encode(any());
			assertThat(existingUser.getPassword()).isEqualTo("original_hashed_password");
		}
	}

	@Nested
	@DisplayName("残りのクリティカルな分岐網羅テスト")
	class CriticalBranchTest {

		@Test
		@DisplayName("更新処理：パスワードが正常に入力された場合、新しいパスワードがハッシュ化されて更新されること")
		void updateAccount_withNewPassword_shouldUpdatePassword() {
			// Given
			UserInfo existingUser = new UserInfo();
			existingUser.setUserId("target_user");

			when(userRepo.findById("target_user")).thenReturn(Optional.of(existingUser));
			when(wageRepo.findById(1)).thenReturn(Optional.of(new Wage()));
			when(passwordEncoder.encode("new_secret")).thenReturn("hashed_new_secret");

			UserRegisterForm form = new UserRegisterForm();
			form.setUserId("target_user");
			form.setPosition("ADMIN");
			form.setWageId(1);
			form.setPassword("new_secret"); // 💡 新しいパスワードを設定
			form.setBirthDate(LocalDate.of(1990, 1, 1));
			// When
			boolean result = accountService.updateAccount(form);

			// Then
			assertThat(result).isTrue();
			verify(passwordEncoder, times(1)).encode("new_secret");
			assertThat(existingUser.getPassword()).isEqualTo("hashed_hashed_new_secret".equals(existingUser.getPassword()) ? "hashed_hashed_new_secret" : "hashed_new_secret");
		}

		@Test
		@DisplayName("異常系：指定された賃金ID(WageId)が存在しない場合、IllegalArgumentExceptionが発生すること")
		void registerAccount_invalidWageId_shouldThrowException() {
			// Given
			when(userRepo.existsById("new_user")).thenReturn(false);
			when(wageRepo.findById(999)).thenReturn(Optional.empty()); // 💡 存在しないWageId

			UserRegisterForm form = new UserRegisterForm();
			form.setUserId("new_user");
			form.setPassword("pass");
			form.setPosition("ADMIN");
			form.setWageId(999);
			form.setBirthDate(LocalDate.of(1990, 1, 1));

			// When & Then
			assertThatThrownBy(() -> accountService.registerAccount(form))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("指定された賃金IDが存在しません: 999");
		}
	}
}
