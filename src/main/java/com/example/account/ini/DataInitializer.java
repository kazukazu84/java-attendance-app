/*
 * ファイルパス: src/main/java/com/example/account/ini/DataInitializer.java
 */
package com.example.account.ini;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.account.entity.Position;
import com.example.account.entity.Wage; // 💡 追加
import com.example.account.repository.UserInfoRepository;
import com.example.account.repository.WageRepository; // 💡 追加
import com.example.account.service.AccountService;

@Component
public class DataInitializer implements CommandLineRunner {
    private final UserInfoRepository userInfoRepository;
    private final WageRepository wageRepository; // 💡 追加
    private final AccountService accountService;

    // コンストラクタ注入（引数に wageRepository を追加）
    public DataInitializer(UserInfoRepository userInfoRepository, 
            WageRepository wageRepository, // 💡 追加
            PasswordEncoder passwordEncoder,
            AccountService accountService) {
        this.userInfoRepository = userInfoRepository;
        this.wageRepository = wageRepository; // 💡 追加
        this.accountService = accountService;
    }

    @Override
    public void run(String... args) throws Exception {

        // 1. 賃金マスタデータの作成（データが空の場合のみ実行）
        if (wageRepository.count() == 0) {
            
            // id=0: 1177円 の登録
            Wage wage0 = new Wage();
            wage0.setWageId(0);
            wage0.setWageValue(1177);
            wageRepository.save(wage0);

            // id=1: 1180円 の登録
            Wage wage1 = new Wage();
            wage1.setWageId(1);
            wage1.setWageValue(1180);
            wageRepository.save(wage1);

            // id=2以降: 1190円から10円刻みで2000円までループ登録
            int currentId = 2;
            int currentValue = 1190;
            
            while (currentValue <= 2000) {
                Wage w = new Wage();
                w.setWageId(currentId);
                w.setWageValue(currentValue);
                wageRepository.save(w);
                
                currentId++;        // IDを1ずつ進める
                currentValue += 10; // 金額を10ずつ上げる
            }
        }

        // 2. 原初ユーザーの作成
        if (userInfoRepository.count() == 0) {
            accountService.registerAccount(
                    "alpha",
                    "hello.world",
                    "神",
                    Position.ADMIN,
                    0, // 💡 これにより、上記で登録した id=0 (1177円) が紐付きます
                    java.sql.Date.valueOf("2026-07-13"),
                    0,
                    false,
                    1);
        }
    }
}