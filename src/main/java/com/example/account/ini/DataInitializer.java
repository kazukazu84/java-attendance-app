package com.example.account.ini;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.account.dto.UserRegisterForm;
import com.example.account.entity.Position;
import com.example.account.entity.Wage;
import com.example.account.repository.UserInfoRepository;
import com.example.account.repository.WageRepository;
import com.example.account.service.AccountService;

@Component
public class DataInitializer implements CommandLineRunner {
    private final UserInfoRepository userInfoRepository;
    private final WageRepository wageRepository;
    private final AccountService accountService;

    // コンストラクタ注入（不要な PasswordEncoder を引数から削除）
    public DataInitializer(UserInfoRepository userInfoRepository, 
            WageRepository wageRepository,
            AccountService accountService) {
        this.userInfoRepository = userInfoRepository;
        this.wageRepository = wageRepository;
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
            // 💡 Dto（UserRegisterForm）オブジェクトを組み立ててサービスに渡す
            UserRegisterForm initUserForm = new UserRegisterForm();
            initUserForm.setUserId("alpha");
            initUserForm.setPassword("hello.world");
            initUserForm.setUserName("神");
            initUserForm.setPosition(Position.ADMIN.name()); // PositionのEnum名（"ADMIN"）をセット
            initUserForm.setWageId(0);                       // 上記で作成した1177円（ID:0）を指定
            initUserForm.setBirthDate(LocalDate.parse("2026-07-13"));
            initUserForm.setEmploymentInsurance(false);
            initUserForm.setIsActive(1);

            accountService.registerAccount(initUserForm);
        }
    }
}