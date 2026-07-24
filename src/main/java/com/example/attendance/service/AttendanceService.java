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

	// 🟢 追加：代わりに共通の LogService を注入します
	@Autowired
	private LogService logService;

	// 👇 追加：UserInfoのステータスを更新するために注入します
	@Autowired
	private UserInfoRepository userRepository;

	// フォーマッターを日付用と時間用に分けます
	private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("M月d日");
	private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H時m分");

	// 現在の勤怠ステータスを取得
	public AttendanceDto getStatus(String userId) {
	    LocalDate today = LocalDate.now();
	    Optional<Attendance> attendanceOpt = attendanceRepository.findByUserIdAndWorkDate(userId, today);

	    if (attendanceOpt.isEmpty()) {
	        // ★ 未出勤なので workDate は today をセット
	        return new AttendanceDto("未出勤です", true, false, today);
	    }

	    Attendance attendance = attendanceOpt.get();
	    String dateStr = attendance.getWorkDate().format(dateFormatter);

	    if (attendance.getClockOut() == null) {
	        String timeStr = attendance.getClockIn().format(timeFormatter);

	        // ★ 出勤中 → workDate をセット
	        return new AttendanceDto(
	                "現在 " + dateStr + timeStr + "～出勤しています",
	                false,
	                true,
	                attendance.getWorkDate()
	        );
	    } else {
	        String timeStr = attendance.getClockOut().format(timeFormatter);

	        // ★ 退勤済み → workDate をセット
	        return new AttendanceDto(
	                "現在 " + dateStr + timeStr + "に退勤しました",
	                false,
	                false,
	                attendance.getWorkDate()
	        );
	    }
	}

	/**
	 * 出勤処理を行い、同時に出勤ログを書き込む
	 */
	@Transactional
	public AttendanceDto clockIn(String userId) {
		// 💡 【追加】現在の状態をチェックし、出勤できない状態ならエラー（例外）を投げる
		AttendanceDto currentStatus = this.getStatus(userId);
		if (!currentStatus.isCanClockIn()) {
			throw new IllegalArgumentException("すでに出勤しているか、本日分の出勤データが存在します");
		}

		// --- 1. 既存の出勤レコード登録処理（省略：現在の実装をここに残す） ---
		Attendance attendance = new Attendance();
		attendance.setUserId(userId);
		attendance.setWorkDate(LocalDate.now());
		attendance.setClockIn(LocalTime.now());
		attendanceRepository.save(attendance);

		//		// --- 2. 【追加】出勤ログを log テーブルに自動登録（messageId = 0 : 出勤） ---
		//		Log clockInLog = new Log();
		//		clockInLog.setMessageId(0); // 0: {user_name}さんが出勤しました
		//		clockInLog.setCreatedAt(LocalDateTime.now());
		//		clockInLog.setTargetUserId(userId);
		//		logRepository.save(clockInLog);

		// --- 2. ⭐共通メソッドへの置き換え ---
		// messageId はマスターに合わせて調整してください（今回はお使いの「0」をそのまま指定）
		logService.saveLog(0, userId); // 👈 1行で安全にログ登録完了！

		// 3. 👈【追加】ユーザーの出退勤ステータスを「1: 出勤状態」に更新
		UserInfo user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("ユーザー情報が見つかりません。ID: " + userId));
		user.setAttendanceStatus(1); // ⭐ 1: 出勤状態
		userRepository.save(user);

		// --- 4. 既存の戻り値処理（省略：現在の実装に合わせてください） ---
		AttendanceDto dto = new AttendanceDto();
		dto.setStatusMessage("出勤中です");
		dto.setCanClockIn(false);
		dto.setCanClockOut(true);
		return dto;
	}

	@Transactional
	public AttendanceDto clockOut(String userId) {

	    // ① 状態チェック
	    AttendanceDto currentStatus = this.getStatus(userId);
	    if (!currentStatus.isCanClockOut()) {
	        throw new IllegalArgumentException("本日の出勤データが見つからないか、すでに退勤しています");
	    }

	    // ② 今日の勤怠を取得
	    Attendance attendance = attendanceRepository
	            .findByUserIdAndWorkDate(userId, LocalDate.now())
	            .orElseThrow(() -> new RuntimeException("本日の出勤データが見つかりません。"));

	    // ③ 退勤時刻をセット
	    attendance.setClockOut(LocalTime.now());
	    attendanceRepository.save(attendance);

	    // ④ ログ保存
	    logService.saveLog(1, userId);

	    // ⑤ ユーザーのステータス更新
	    UserInfo user = userRepository.findById(userId)
	            .orElseThrow(() -> new RuntimeException("ユーザー情報が見つかりません。ID: " + userId));
	    user.setAttendanceStatus(0);
	    userRepository.save(user);

	    // ⑥ ★ workDate を DTO にセット（給与計算に必須）
	    AttendanceDto dto = new AttendanceDto();
	    dto.setStatusMessage("退勤しました");
	    dto.setCanClockIn(false);
	    dto.setCanClockOut(false);
	    dto.setWorkDate(attendance.getWorkDate());   // ★ これが無いと 500 になる

	    return dto;
	}

}
