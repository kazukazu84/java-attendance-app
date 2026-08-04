package com.example.account.service;

import java.time.LocalDate;
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
import com.example.attendance.service.AttendanceService;
import com.example.salary.service.SalaryCalculationService;

@Service
public class AccountService {

	@Autowired
	private UserInfoRepository userRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private WageRepository wageRepo;
	
	@Autowired
	private AttendanceService attendanceService;


	@Autowired
	private SalaryCalculationService salaryCalculationService;


	public AccountService(
	        UserInfoRepository userRepo,
	        WageRepository wageRepo,
	        PasswordEncoder passwordEncoder,
	        AttendanceService attendanceService) {

	    this.userRepo = userRepo;
	    this.wageRepo = wageRepo;
	    this.passwordEncoder = passwordEncoder;
	    this.attendanceService = attendanceService;
	}

	/**
	 * IDによるユーザー検索
	 */
	public Optional<UserInfo> findUserById(String id) {
		return userRepo.findById(id);
	}


	/**
	 * 賃金マスター取得
	 */
	public List<Wage> getAllWages() {
		return wageRepo.findAllByOrderByWageValueAsc();
	}


	/**
	 * 編集用Form作成
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
			java.sql.Date sqlDate =
					new java.sql.Date(userInfo.getBirthDate().getTime());

			form.setBirthDate(sqlDate.toLocalDate());
		}


		form.setEmploymentInsurance(
				userInfo.isEmploymentInsurance());

		form.setIsActive(
				userInfo.getIsActive());


		// 編集画面ではパスワードを表示しない
		form.setPassword("＊＊＊＊＊＊＊＊");


		return form;
	}



	/**
	 * ユーザーID存在確認
	 */
	public boolean existsByUserId(String id) {
		return userRepo.existsById(id);
	}



	/**
	 * 新規登録
	 */
	@Transactional
	public void registerAccount(UserRegisterForm form) {


		if (existsByUserId(form.getUserId())) {
			throw new IllegalArgumentException(
					"このユーザーIDは既に登録されています。");
		}


		UserInfo user = new UserInfo();


		user.setUserId(form.getUserId());

		user.setPassword(
				passwordEncoder.encode(form.getPassword()));

		user.setUserName(form.getUserName());

		user.setPosition(
				Position.valueOf(form.getPosition()));



		Wage wage = wageRepo.findById(form.getWageId())
				.orElseThrow(() ->
				new IllegalArgumentException(
						"指定された賃金IDが存在しません"));

		user.setWage(wage);



		if(form.getBirthDate()!=null) {
			user.setBirthDate(
					java.sql.Date.valueOf(form.getBirthDate()));
		}


		// 初期状態は退勤
		user.setAttendanceStatus(0);

		user.setEmploymentInsurance(
				form.isEmploymentInsurance());


		user.setIsActive(
				form.getIsActive());



		userRepo.save(user);
	}




	/**
	 * 既存ユーザー更新
	 */
	@Transactional
	public boolean updateAccount(UserRegisterForm form) {


		Optional<UserInfo> userOpt =
				userRepo.findById(form.getUserId());


		if(userOpt.isEmpty()) {
			return false;
		}


		UserInfo user = userOpt.get();


		/*
		 * 有効 → 無効変更
		 * かつ現在出勤中
		 */
		boolean deactivate =
				user.getIsActive() == 1
				&& form.getIsActive() == 0;


		if(deactivate
				&& user.getAttendanceStatus() == 1) {


			// 強制退勤
			attendanceService.forceClockOut(
					user.getUserId());


			// 給与再計算
			LocalDate today = LocalDate.now();

			salaryCalculationService
				.calculateOrUpdateMonthlySalary(
						user.getUserId(),
						today.getYear(),
						today.getMonthValue());
		}




		user.setUserName(
				form.getUserName());


		user.setPosition(
				Position.valueOf(form.getPosition()));



		Wage wage = wageRepo.findById(form.getWageId())
				.orElseThrow(() ->
				new IllegalArgumentException(
						"指定された賃金IDが存在しません"));


		user.setWage(wage);



		user.setBirthDate(
				java.sql.Date.valueOf(
						form.getBirthDate()));



		user.setEmploymentInsurance(
				form.isEmploymentInsurance());



		user.setIsActive(
				form.getIsActive());



		// パスワード変更時のみ更新
		if(form.getPassword()!=null
				&& !form.getPassword().trim().isEmpty()
				&& !form.getPassword()
				.equals("＊＊＊＊＊＊＊＊")) {


			user.setPassword(
					passwordEncoder.encode(
							form.getPassword()));
		}



		userRepo.save(user);


		return true;
	}




	/**
	 * 一括無効化
	 */
	@Transactional
	public void deactivateUsers(List<String> userIds) {


		List<UserInfo> users =
				userRepo.findAllById(userIds);



		for(UserInfo user : users) {


			if(user.getIsActive()==1
					&& user.getAttendanceStatus()==1) {


				attendanceService.forceClockOut(
						user.getUserId());


				LocalDate today = LocalDate.now();


				salaryCalculationService
					.calculateOrUpdateMonthlySalary(
							user.getUserId(),
							today.getYear(),
							today.getMonthValue());
			}



			user.setIsActive(0);
		}


		userRepo.saveAll(users);
	}




	/**
	 * 検索
	 */
	public List<UserInfo> searchUsers(
			String keyword,
			String type) {


		if("id".equals(type)) {

			return userRepo.findByUserIdContaining(keyword);

		}else if("name".equals(type)) {

			return userRepo.findByUserNameContaining(keyword);
		}


		return userRepo.findAll();
	}
}