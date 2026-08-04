package com.example.attendance.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.account.entity.UserInfo;
import com.example.account.repository.UserInfoRepository;
import com.example.attendance.dto.AttendanceDto;
import com.example.attendance.entity.Attendance;
import com.example.attendance.repository.AttendanceRepository;
import com.example.main.service.LogService;
import com.example.salary.service.SalaryCalculationService;

@Service
public class AttendanceService {


    @Autowired
    private AttendanceRepository attendanceRepository;


    @Autowired
    private UserInfoRepository userRepository;


    @Autowired
    private LogService logService;


    @Autowired
    private SalaryCalculationService salaryCalculationService;



    private final DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern("M月d日");


    private final DateTimeFormatter timeFormatter =
            DateTimeFormatter.ofPattern("H時m分");



    /**
     * 現在の勤怠状態取得
     *
     * 夜勤対応：
     * 今日ではなく、未退勤の最新レコードを優先する
     */
    public AttendanceDto getStatus(String userId) {


        /*
         * ① 未退勤の勤怠を検索
         *
         * 日付をまたいだ勤務でも
         * ここで取得できる
         */
        Optional<Attendance> workingOpt =
                attendanceRepository
                .findFirstByUserIdAndClockOutIsNullOrderByWorkDateDesc(userId);



        if (workingOpt.isPresent()) {


            Attendance attendance =
                    workingOpt.get();


            String dateStr =
                    attendance.getWorkDate()
                              .format(dateFormatter);


            String timeStr =
                    attendance.getClockIn()
                              .format(timeFormatter);



            return new AttendanceDto(
                    "現在 " + dateStr + timeStr + "～出勤しています",
                    false,
                    true,
                    attendance.getWorkDate()
            );
        }



        /*
         * ② 未出勤の場合
         */
        LocalDate today = LocalDate.now();


        Optional<Attendance> attendanceOpt =
                attendanceRepository
                .findByUserIdAndWorkDate(
                        userId,
                        today
                );



        if (attendanceOpt.isEmpty()) {


            return new AttendanceDto(
                    "未出勤です",
                    true,
                    false,
                    today
            );
        }



        /*
         * ③ 退勤済みの場合
         */
        Attendance attendance =
                attendanceOpt.get();



        String dateStr =
                attendance.getWorkDate()
                          .format(dateFormatter);


        String timeStr =
                attendance.getClockOut()
                          .format(timeFormatter);



        return new AttendanceDto(
                "現在 " + dateStr + timeStr + "に退勤しました",
                false,
                false,
                attendance.getWorkDate()
        );
    }






    /**
     * 出勤処理
     */
    @Transactional
    public AttendanceDto clockIn(String userId) {



        /*
         * 前日の未退勤チェック
         *
         * 夜勤終了前の二重出勤防止
         */
        Optional<Attendance> workingOpt =
                attendanceRepository
                .findFirstByUserIdAndClockOutIsNullOrderByWorkDateDesc(
                        userId
                );


        if (workingOpt.isPresent()) {


            throw new IllegalArgumentException(
                    "未退勤の勤務があります。先に退勤してください。"
            );
        }




        /*
         * 今日すでに退勤済みの場合も禁止
         */
        AttendanceDto currentStatus =
                this.getStatus(userId);



        if (!currentStatus.isCanClockIn()) {


            throw new IllegalArgumentException(
                    "すでに出勤しているか、本日の出勤データが存在します"
            );
        }




        /*
         * 勤怠登録
         */
        Attendance attendance =
                new Attendance();


        attendance.setUserId(userId);


        attendance.setWorkDate(
                LocalDate.now()
        );


        attendance.setClockIn(
                LocalTime.now().withNano(0)
        );


        attendanceRepository.save(attendance);




        /*
         * 出勤ログ
         */
        logService.saveLog(0, userId);




        /*
         * UserInfoの状態更新
         */
        UserInfo user =
                userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "ユーザー情報が見つかりません。ID:"
                                + userId
                        )
                );


        user.setAttendanceStatus(1);


        userRepository.save(user);




        AttendanceDto dto =
                new AttendanceDto();


        dto.setStatusMessage("出勤中です");


        dto.setCanClockIn(false);


        dto.setCanClockOut(true);


        dto.setWorkDate(
                attendance.getWorkDate()
        );


        return dto;
    }
    /**
     * 退勤処理
     *
     * 通常退勤・夜勤退勤対応
     */
    @Transactional
    public AttendanceDto clockOut(String userId) {


        /*
         * 現在出勤中の勤怠を取得
         *
         * 今日ではなく未退勤データを見るため、
         * 日付をまたいでも対応可能
         */
        Attendance attendance =
                attendanceRepository
                .findFirstByUserIdAndClockOutIsNullOrderByWorkDateDesc(
                        userId
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "退勤対象の勤怠がありません。"
                        )
                );



        /*
         * 退勤時刻セット
         */
        attendance.setClockOut(
                LocalTime.now().withNano(0)
        );


        attendanceRepository.save(attendance);




        /*
         * 退勤ログ
         */
        logService.saveLog(1, userId);




        /*
         * UserInfoの出勤状態更新
         */
        UserInfo user =
                userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "ユーザー情報が見つかりません。ID:"
                                + userId
                        )
                );


        user.setAttendanceStatus(0);


        userRepository.save(user);




        /*
         * 給与再計算
         *
         * 夜勤の場合でも勤務開始日の月で計算
         */
        salaryCalculationService
                .calculateOrUpdateMonthlySalary(
                        userId,
                        attendance.getWorkDate().getYear(),
                        attendance.getWorkDate().getMonthValue()
                );




        AttendanceDto dto =
                new AttendanceDto();


        dto.setStatusMessage("退勤しました");


        dto.setCanClockIn(false);


        dto.setCanClockOut(false);


        dto.setWorkDate(
                attendance.getWorkDate()
        );


        return dto;
    }







    /**
     * 管理者による強制退勤
     *
     * 在籍ステータスを無効にするとき使用
     */
    @Transactional
    public void forceClockOut(String userId) {



        /*
         * 未退勤の勤務を取得
         */
        Optional<Attendance> attendanceOpt =
                attendanceRepository
                .findFirstByUserIdAndClockOutIsNullOrderByWorkDateDesc(
                        userId
                );



        /*
         * 出勤中でなければ何もしない
         */
        if (attendanceOpt.isEmpty()) {

            return;
        }



        Attendance attendance =
                attendanceOpt.get();




        /*
         * 現在時刻で退勤
         */
        attendance.setClockOut(
                LocalTime.now().withNano(0)
        );


        attendanceRepository.save(attendance);




        /*
         * 退勤ログ
         */
        logService.saveLog(1, userId);




        /*
         * UserInfo状態変更
         */
        UserInfo user =
                userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "ユーザー情報が見つかりません。ID:"
                                + userId
                        )
                );


        user.setAttendanceStatus(0);


        userRepository.save(user);




        /*
         * 強制退勤でも給与再計算
         */
        salaryCalculationService
                .calculateOrUpdateMonthlySalary(
                        userId,
                        attendance.getWorkDate().getYear(),
                        attendance.getWorkDate().getMonthValue()
                );
    }

}