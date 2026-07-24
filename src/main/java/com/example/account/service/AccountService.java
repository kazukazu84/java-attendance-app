package com.example.account.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.account.dto.UserRegisterForm;
import com.example.account.entity.Position;
import com.example.account.entity.UserInfo;
import com.example.account.entity.Wage;
import com.example.account.repository.UserInfoRepository;
import com.example.account.repository.WageRepository;

@Service
public class AccountService {
	@Autowired private UserInfoRepository userRepo;
	@Autowired private PasswordEncoder passwordEncoder;
	@Autowired private WageRepository wageRepo;

	public AccountService(UserInfoRepository userRepo, 
			WageRepository wageRepo, 
			PasswordEncoder passwordEncoder) {
		this.userRepo = userRepo;
		this.wageRepo = wageRepo;
		this.passwordEncoder = passwordEncoder;
	}

	/**
	 * IDによるユーザー検索
	 */
	public Optional<UserInfo> findUserById(String id) {
		return userRepo.findById(id);
	}

	/**
	 * 賃金マスターの全件取得（昇順）
	 */
	public List<Wage> getAllWages() {
		return wageRepo.findAllByOrderByWageValueAsc();
	}

	/**
	 * 編集用 Form オブジェクトの生成
	 */
	public UserRegisterForm getEditForm(UserInfo userInfo) {
		UserRegisterForm form = new UserRegisterForm();
		form.setUserId(userInfo.getUserId());
		form.setUserName(userInfo.getUserName());
		form.setPosition(userInfo.getPosition().name());

		if (userInfo.getWage() != null) {
			form.setWageId(userInfo.getWage().getWageId());
		}

		if (userInfo.getBirthDate() != null) {
			java.sql.Date sqlDate = new java.sql.Date(userInfo.getBirthDate().getTime());
			form.setBirthDate(sqlDate.toLocalDate());
		}

		form.setEmploymentInsurance(userInfo.isEmploymentInsurance());
		form.setIsActive(userInfo.getIsActive());
		form.setPassword("＊＊＊＊＊＊＊＊"); // マスク処理

		return form;
	}

	/**
	 * 💡 ユーザーIDが既に存在するか確認する
	 */
	public boolean existsByUserId(String id) {
		return userRepo.existsById(id);
	}

	/**
	 * 💡 新規アカウント登録（UserRegisterFormを直接受け取るように修正）
	 */
	@Transactional
	public void registerAccount(UserRegisterForm form
			//,, String currentUserId
			//ログを残す場合に利用する。
			) {	

		if (existsByUserId(form.getUserId())) {
			throw new IllegalArgumentException("このユーザーIDは既に登録されています。");
		}
		UserInfo user = new UserInfo();

		user.setUserId(form.getUserId());
		user.setPassword(passwordEncoder.encode(form.getPassword()));
		user.setUserName(form.getUserName());
		user.setPosition(Position.valueOf(form.getPosition()));

		Wage wage = wageRepo.findById(form.getWageId())
				.orElseThrow(() -> new IllegalArgumentException("指定された賃金IDが存在しません: " + form.getWageId()));
		user.setWage(wage);

		if (form.getBirthDate() != null) {
			user.setBirthDate(java.sql.Date.valueOf(form.getBirthDate()));
		}

		user.setAttendanceStatus(0); // 勤怠状況の初期値
		user.setEmploymentInsurance(form.isEmploymentInsurance());
		user.setIsActive(form.getIsActive());

		userRepo.save(user);

		// ログ保存
		//logService.saveLog(XX, currentUserId);
	}

	/**
	 * 既存アカウント更新
	 * @return 更新成否
	 */
	@Transactional
	public boolean updateAccount(UserRegisterForm form
			//, String currentUserId
			//ログを残す場合に利用する。
			) {
		Optional<UserInfo> userOpt = userRepo.findById(form.getUserId());
		if (userOpt.isEmpty()) {
			return false;
		}

		UserInfo user = userOpt.get();

		user.setUserName(form.getUserName());
		user.setPosition(Position.valueOf(form.getPosition()));

		Wage wage = wageRepo.findById(form.getWageId())
				.orElseThrow(() -> new IllegalArgumentException("指定された賃金IDが存在しません: " + form.getWageId()));
		user.setWage(wage);

		//if (form.getBirthDate() != null) {
		user.setBirthDate(java.sql.Date.valueOf(form.getBirthDate()));
		//}

		user.setEmploymentInsurance(form.isEmploymentInsurance());
		user.setIsActive(form.getIsActive());

		// 💡 空文字、または初期表示の「＊＊＊＊＊＊＊＊」のままの場合はパスワード更新をスキップする
		if (form.getPassword() != null && !form.getPassword().trim().isEmpty() && !form.getPassword().equals("＊＊＊＊＊＊＊＊")) {
			user.setPassword(passwordEncoder.encode(form.getPassword()));
		}

		userRepo.save(user);

		// ログ保存
		//logService.saveLog(XX, currentUserId);

		return true;
	}

	/**
     * ユーザー一括無効化
     */
	@Transactional
	public void deactivateUsers(List<String> userIds
			//		, String currentUserId
			) {
		List<UserInfo> users = userRepo.findAllById(userIds);
		for (UserInfo user : users) {
			user.setIsActive(0);
		}
		userRepo.saveAll(users);

		// ログ保存
		//logService.saveLog(XX, currentUserId);

	}

	/**
	 * 検索処理
	 */
	public List<UserInfo> searchUsers(String keyword, String type) {
		if ("id".equals(type)) {
			return userRepo.findByUserIdContaining(keyword);
		} else if ("name".equals(type)) {
			return userRepo.findByUserNameContaining(keyword);
		}
		return userRepo.findAll();
	}
}