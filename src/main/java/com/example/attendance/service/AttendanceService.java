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

@Service
public class AttendanceService {


    @Autowired
    private AttendanceRepository attendanceRepository;


    @Autowired
    private LogService logService;

<<<<<<< HEAD
=======
	// 現在の勤怠ステータスを取得
//	public AttendanceDto getStatus(String userId) {
//	    LocalDate today = LocalDate.now();
//	    Optional<Attendance> attendanceOpt = attendanceRepository.findByUserIdAndWorkDate(userId, today);
//
//	    if (attendanceOpt.isEmpty()) {
//	        // ★ 未出勤なので workDate は today をセット
//	        return new AttendanceDto("未出勤です", true, false, today);
//	    }
//
//	    Attendance attendance = attendanceOpt.get();
//	    String dateStr = attendance.getWorkDate().format(dateFormatter);
//
//	    if (attendance.getClockOut() == null) {
//	        String timeStr = attendance.getClockIn().format(timeFormatter);
//
//	        // ★ 出勤中 → workDate をセット
//	        return new AttendanceDto(
//	                "現在 " + dateStr + timeStr + "～出勤しています",
//	                false,
//	                true,
//	                attendance.getWorkDate()
//	        );
//	    } else {
//	        String timeStr = attendance.getClockOut().format(timeFormatter);
//
//	        // ★ 退勤済み → workDate をセット
//	        return new AttendanceDto(
//	                "現在 " + dateStr + timeStr + "に退勤しました",
//	                false,
//	                false,
//	                attendance.getWorkDate()
//	        );
//	    }
//	}
	
	public AttendanceDto getStatus(String userId) {
>>>>>>> refs/heads/master

<<<<<<< HEAD
    @Autowired
    private UserInfoRepository userRepository;
=======
	    // ① 未退勤レコードを探す
	    Optional<Attendance> workingOpt =
	            attendanceRepository.findFirstByUserIdAndClockOutIsNullOrderByWorkDateDesc(userId);
>>>>>>> refs/heads/master

<<<<<<< HEAD
=======
	    if (workingOpt.isPresent()) {
>>>>>>> refs/heads/master

<<<<<<< HEAD
=======
	        Attendance attendance = workingOpt.get();

	        String dateStr = attendance.getWorkDate().format(dateFormatter);
	        String timeStr = attendance.getClockIn().format(timeFormatter);
>>>>>>> refs/heads/master

<<<<<<< HEAD
    private final DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern("M月d日");

=======
	        return new AttendanceDto(
	                "現在 " + dateStr + timeStr + "～出勤しています",
	                false,
	                true,
	                attendance.getWorkDate()
	        );
	    }

	    // ② 今日のレコードを探す
	    LocalDate today = LocalDate.now();

	    Optional<Attendance> attendanceOpt =
	            attendanceRepository.findByUserIdAndWorkDate(userId, today);

	    if (attendanceOpt.isEmpty()) {
	        return new AttendanceDto("未出勤です", true, false, today);
	    }

	    Attendance attendance = attendanceOpt.get();

	    String dateStr = attendance.getWorkDate().format(dateFormatter);
	    String timeStr = attendance.getClockOut().format(timeFormatter);

	    return new AttendanceDto(
	            "現在 " + dateStr + timeStr + "に退勤しました",
	            false,
	            false,
	            attendance.getWorkDate()
	    );
	}
>>>>>>> refs/heads/master

<<<<<<< HEAD
    private final DateTimeFormatter timeFormatter =
            DateTimeFormatter.ofPattern("H時m分");
=======
	/**
	 * 出勤処理を行い、同時に出勤ログを書き込む
	 */
	@Transactional
	public AttendanceDto clockIn(String userId) {
		
		// 前日の未退勤チェック
		if (attendanceRepository
		        .findFirstByUserIdAndClockOutIsNullOrderByWorkDateDesc(userId)
		        .isPresent()) {

		    throw new IllegalArgumentException(
		            "未退勤の勤務があります。先に退勤してください。");
		}
		
		// 💡 【追加】現在の状態をチェックし、出勤できない状態ならエラー（例外）を投げる
		AttendanceDto currentStatus = this.getStatus(userId);
		if (!currentStatus.isCanClockIn()) {
			throw new IllegalArgumentException("すでに出勤しているか、本日分の出勤データが存在します");
		}
>>>>>>> refs/heads/master



    /**
     * 現在の勤怠状態取得
     */
    public AttendanceDto getStatus(String userId) {


        LocalDate today = LocalDate.now();


        Optional<Attendance> attendanceOpt =
                attendanceRepository.findByUserIdAndWorkDate(
                        userId,
                        today
                );

<<<<<<< HEAD
=======
	    // ② 今日の勤怠を取得
	    
	    Attendance attendance =
	            attendanceRepository
	                .findFirstByUserIdAndClockOutIsNullOrderByWorkDateDesc(userId)
	                .orElseThrow(() ->
	                    new RuntimeException("退勤対象の勤怠が見つかりません。"));
//	    Attendance attendance = attendanceRepository
//	            .findByUserIdAndWorkDate(userId, LocalDate.now())
//	            .orElseThrow(() -> new RuntimeException("本日の出勤データが見つかりません。"));
>>>>>>> refs/heads/master

        if (attendanceOpt.isEmpty()) {

            return new AttendanceDto(
                    "未出勤です",
                    true,
                    false,
                    today
            );
        }


<<<<<<< HEAD
=======
//	    // ⑥ ★ workDate を DTO にセット（給与計算に必須）
//	    AttendanceDto dto = new AttendanceDto();
//	    dto.setStatusMessage("退勤しました");
//	    dto.setCanClockIn(false);
//	    dto.setCanClockOut(false);
//	    dto.setWorkDate(attendance.getWorkDate());   // ★ これが無いと 500 になる
//
//	    return dto;
	    
//	    ⑥夜勤判定のためコード置き換え(8/4 桝田)
	    AttendanceDto dto = getStatus(userId);
>>>>>>> refs/heads/master

<<<<<<< HEAD
        Attendance attendance =
                attendanceOpt.get();
=======
	 // 給与計算用なので勤務開始日は保持
	 dto.setWorkDate(attendance.getWorkDate());

	 return dto;
	}
>>>>>>> refs/heads/master



        String dateStr =
                attendance.getWorkDate()
                          .format(dateFormatter);



        // 出勤中
        if (attendance.getClockOut() == null) {


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


        // 退勤済み
        else {


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
    }





    /**
     * 出勤処理
     */
    @Transactional
    public AttendanceDto clockIn(String userId) {


        AttendanceDto currentStatus =
                this.getStatus(userId);



        if (!currentStatus.isCanClockIn()) {

            throw new IllegalArgumentException(
                    "すでに出勤しているか、本日分の出勤データが存在します"
            );
        }



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



        // 出勤ログ
        logService.saveLog(0, userId);




        UserInfo user =
                userRepository.findById(userId)
                .orElseThrow(() ->
                    new RuntimeException(
                        "ユーザー情報が見つかりません。ID:" + userId
                    )
                );



        user.setAttendanceStatus(1);


        userRepository.save(user);




        AttendanceDto dto =
                new AttendanceDto();


        dto.setStatusMessage("出勤中です");


        dto.setCanClockIn(false);


        dto.setCanClockOut(true);



        return dto;
    }







    /**
     * 通常退勤処理
     */
    @Transactional
    public AttendanceDto clockOut(String userId) {


        AttendanceDto currentStatus =
                this.getStatus(userId);



        if (!currentStatus.isCanClockOut()) {


            throw new IllegalArgumentException(
                    "本日の出勤データが見つからないか、すでに退勤しています"
            );
        }




        Attendance attendance =
                attendanceRepository
                .findByUserIdAndWorkDate(
                        userId,
                        LocalDate.now()
                )
                .orElseThrow(() ->
                    new RuntimeException(
                        "本日の出勤データが見つかりません。"
                    )
                );



        attendance.setClockOut(
                LocalTime.now().withNano(0)
        );


        attendanceRepository.save(attendance);



        // 退勤ログ
        logService.saveLog(1, userId);




        UserInfo user =
                userRepository.findById(userId)
                .orElseThrow(() ->
                    new RuntimeException(
                        "ユーザー情報が見つかりません。ID:" + userId
                    )
                );



        user.setAttendanceStatus(0);


        userRepository.save(user);




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
     * 在籍ステータス無効化時などに使用
     */
    @Transactional
    public void forceClockOut(String userId) {



        Optional<Attendance> attendanceOpt =
                attendanceRepository
                .findFirstByUserIdAndClockOutIsNullOrderByWorkDateDesc(
                        userId
                );



        // 出勤中でない場合は何もしない
        if (attendanceOpt.isEmpty()) {
            return;
        }



        Attendance attendance =
                attendanceOpt.get();




        attendance.setClockOut(
                LocalTime.now().withNano(0)
        );



        attendanceRepository.save(attendance);




        // 退勤ログ
        logService.saveLog(1, userId);




        UserInfo user =
                userRepository.findById(userId)
                .orElseThrow(() ->
                    new RuntimeException(
                        "ユーザー情報が見つかりません。ID:" + userId
                    )
                );



        user.setAttendanceStatus(0);


        userRepository.save(user);
    }

}