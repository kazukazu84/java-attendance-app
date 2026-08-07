package com.example.account.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.account.dto.UserRegisterForm;
import com.example.account.entity.Position;
import com.example.account.entity.UserInfo;
import com.example.account.entity.Wage;
import com.example.account.repository.UserInfoRepository;
import com.example.account.repository.WageRepository;
import com.example.adminshift.entity.Shift;
import com.example.adminshift.entity.ShiftApplicationEvent;
import com.example.adminshift.repository.ShiftApplicationEventRepository;
import com.example.adminshift.repository.ShiftRepository;
import com.example.attendance.service.AttendanceService;


@Service
public class AccountService {


    @Autowired
    private UserInfoRepository userRepo;


    @Autowired
    private PasswordEncoder passwordEncoder;


    @Autowired
    private WageRepository wageRepo;



    /*
     * 在籍変更時の強制退勤処理用
     *
     * AccountService
     *        ↓
     * AttendanceService
     *
     * 循環回避
     */
    @Autowired
    @Lazy
    private AttendanceService attendanceService;



    /*
     * 新規ユーザー登録時
     * 既存イベント取得用
     */
    @Autowired
    private ShiftApplicationEventRepository shiftApplicationEventRepository;



    /*
     * 新規ユーザー登録時
     * シフト作成用
     */
    @Autowired
    private ShiftRepository shiftRepository;





    public AccountService(
            UserInfoRepository userRepo,
            WageRepository wageRepo,
            PasswordEncoder passwordEncoder,
            @Lazy AttendanceService attendanceService,
            ShiftApplicationEventRepository shiftApplicationEventRepository,
            ShiftRepository shiftRepository) {


        this.userRepo = userRepo;

        this.wageRepo = wageRepo;

        this.passwordEncoder = passwordEncoder;

        this.attendanceService = attendanceService;

        this.shiftApplicationEventRepository =
                shiftApplicationEventRepository;

        this.shiftRepository =
                shiftRepository;
    }








    /**
     * ID検索
     */
    public Optional<UserInfo> findUserById(String id) {

        return userRepo.findById(id);
    }








    /**
     * 賃金一覧取得
     */
    public List<Wage> getAllWages() {

        return wageRepo.findAllByOrderByWageValueAsc();
    }








    /**
     * 編集画面用Form生成
     */
    public UserRegisterForm getEditForm(UserInfo userInfo) {


        UserRegisterForm form =
                new UserRegisterForm();



        form.setUserId(
                userInfo.getUserId()
        );


        form.setUserName(
                userInfo.getUserName()
        );



        form.setPosition(
                userInfo.getPosition().name()
        );



        if (userInfo.getWage() != null) {


            form.setWageId(
                    userInfo.getWage().getWageId()
            );
        }




        if (userInfo.getBirthDate() != null) {


            java.sql.Date sqlDate =
                    new java.sql.Date(
                            userInfo.getBirthDate().getTime()
                    );


            form.setBirthDate(
                    sqlDate.toLocalDate()
            );
        }




        form.setEmploymentInsurance(
                userInfo.isEmploymentInsurance()
        );



        form.setIsActive(
                userInfo.getIsActive()
        );



        /*
         * 表示用マスク
         */
        form.setPassword(
                "＊＊＊＊＊＊＊＊"
        );



        return form;
    }









    /**
     * ユーザー存在確認
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
                    "このユーザーIDは既に登録されています。"
            );
        }



        UserInfo user =
                new UserInfo();



        user.setUserId(
                form.getUserId()
        );


        user.setPassword(
                passwordEncoder.encode(
                        form.getPassword()
                )
        );


        user.setUserName(
                form.getUserName()
        );



        user.setPosition(
                Position.valueOf(
                        form.getPosition()
                )
        );




        Wage wage =
                wageRepo.findById(
                        form.getWageId()
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "指定された賃金IDが存在しません"
                        )
                );



        user.setWage(wage);




        if (form.getBirthDate() != null) {


            user.setBirthDate(
                    java.sql.Date.valueOf(
                            form.getBirthDate()
                    )
            );
        }




        user.setAttendanceStatus(0);



        user.setEmploymentInsurance(
                form.isEmploymentInsurance()
        );



        user.setIsActive(
                form.getIsActive()
        );




        /*
         * ユーザー保存
         */
        userRepo.save(user);




        /*
         * 新規ユーザー登録時
         *
         * 既存イベント分の
         * シフト初期レコード作成
         */
        createShiftForNewUser(user);

    }








    /**
     * 新規登録ユーザー用
     *
     * 既存イベントのシフト初期データ作成
     *
     * 仕様：
     *
     * 登録日以前に終了したイベント
     * → 作成しない
     *
     * 登録日を含むイベント
     * → 登録日から作成
     *
     * 登録日より未来のイベント
     * → 開始日から作成
     */
    private void createShiftForNewUser(UserInfo user) {


        if (user == null
                || user.getUserId() == null) {

            return;
        }



        LocalDate registerDate =
                LocalDate.now();



        List<ShiftApplicationEvent> events =
                shiftApplicationEventRepository.findAll();




        System.out.println(
                "対象イベント数：" 
                + events.size()
        );



        List<Shift> shifts =
                new ArrayList<>();




        int createCount = 0;





        for (ShiftApplicationEvent event : events) {



            if (event.getTargetStartDate() == null
                    || event.getTargetEndDate() == null) {

                continue;
            }





            /*
             * 登録日前に終了済みイベント
             */
            if (event.getTargetEndDate()
                    .isBefore(registerDate)) {


                continue;
            }






            LocalDate startDate =
                    event.getTargetStartDate();





            /*
             * イベント途中で登録した場合
             *
             * 開始日ではなく登録日から作成
             */
            if (startDate.isBefore(registerDate)) {

                startDate = registerDate;
            }





            LocalDate current =
                    startDate;





            while (!current.isAfter(
                    event.getTargetEndDate())) {




                /*
                 * 二重作成防止
                 */
                boolean exists =
                        shiftRepository
                        .findByEventIdAndUserIdAndShiftDate(
                                event.getEventId(),
                                user.getUserId(),
                                current)
                        .isPresent();




                if (!exists) {


                    Shift shift =
                            new Shift();



                    shift.setEventId(
                            event.getEventId()
                    );



                    shift.setUserId(
                            user.getUserId()
                    );



                    shift.setShiftDate(
                            current
                    );



                    /*
                     * 初期状態
                     *
                     * 1 = 出勤可能
                     */
                    shift.setIsAvailable(1);




                    shifts.add(shift);



                    createCount++;

                }




                current =
                        current.plusDays(1);
            }



        }





        if (!shifts.isEmpty()) {


            shiftRepository.saveAll(shifts);

        }





        System.out.println(
                "新規ユーザーシフト作成:"
                + user.getUserId()
                + " 件数="
                + createCount
        );

    }
    
    /**
     * アカウント更新
     */
    @Transactional
    public boolean updateAccount(UserRegisterForm form) {


        Optional<UserInfo> userOpt =
                userRepo.findById(
                        form.getUserId()
                );



        if (userOpt.isEmpty()) {

            return false;
        }




        UserInfo user =
                userOpt.get();





        /*
         * 有効 → 無効変更
         *
         * 出勤中なら強制退勤
         */
        if (user.getIsActive() == 1
                && form.getIsActive() == 0
                && user.getAttendanceStatus() == 1) {



            attendanceService.forceClockOut(
                    user.getUserId()
            );

        }





        user.setUserName(
                form.getUserName()
        );



        user.setPosition(
                Position.valueOf(
                        form.getPosition()
                )
        );





        Wage wage =
                wageRepo.findById(
                        form.getWageId()
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "指定された賃金IDが存在しません"
                        )
                );



        user.setWage(wage);





        if (form.getBirthDate() != null) {


            user.setBirthDate(
                    java.sql.Date.valueOf(
                            form.getBirthDate()
                    )
            );

        }





        user.setEmploymentInsurance(
                form.isEmploymentInsurance()
        );



        user.setIsActive(
                form.getIsActive()
        );






        /*
         * パスワード変更時のみ更新
         */
        if (form.getPassword() != null
                && !form.getPassword().trim().isEmpty()
                && !form.getPassword().equals("＊＊＊＊＊＊＊＊")) {



            user.setPassword(
                    passwordEncoder.encode(
                            form.getPassword()
                    )
            );

        }





        userRepo.save(user);



        return true;

    }









    /**
     * ユーザー一括無効化
     */
    @Transactional
    public void deactivateUsers(List<String> userIds) {



        List<UserInfo> users =
                userRepo.findAllById(userIds);





        for (UserInfo user : users) {



            /*
             * 出勤中なら強制退勤
             */
            if (user.getAttendanceStatus() == 1) {



                attendanceService.forceClockOut(
                        user.getUserId()
                );

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



        if ("id".equals(type)) {


            return userRepo.findByUserIdContaining(
                    keyword
            );



        } else if ("name".equals(type)) {



            return userRepo.findByUserNameContaining(
                    keyword
            );

        }




        return userRepo.findAll();

    }

}