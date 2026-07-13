package com.example.account.ini;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.account.entity.Position;
import com.example.account.repository.UserInfoRepository;
import com.example.account.service.AccountService;

@Component
public class DataInitializer implements CommandLineRunner {
	private final UserInfoRepository userInfoRepository;
	private AccountService accountService;

	// コンストラクタ注入
	public DataInitializer(UserInfoRepository userInfoRepository, 
			PasswordEncoder passwordEncoder,
			AccountService accountService) {
		this.userInfoRepository = userInfoRepository;
		this.accountService = accountService;
	}

	@Override
	public void run(String... args) throws Exception {

		// 2. 原初ユーザーの作成
		if (userInfoRepository.count() == 0) {
			accountService.registerAccount(
					"alpha",
				    "hello.world",
				    "神",
				    Position.ADMIN,
				    0,
				    java.sql.Date.valueOf("2026-07-13"),
				    0,
				    false,
				    1);
		}
	}

}
