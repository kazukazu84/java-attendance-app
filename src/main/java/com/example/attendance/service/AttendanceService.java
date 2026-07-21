package com.example.attendance.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.attendance.dto.AttendanceDto;
import com.example.attendance.entity.Attendance;
import com.example.attendance.entity.TempUserInfo;
import com.example.attendance.repository.AttendanceRepository;
import com.example.attendance.repository.TempUserInfoRepository;
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
	private TempUserInfoRepository userRepository;

	// フォーマッターを日付用と時間用に分けます
	private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("M月d日");
	private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H時m分");

	// 現在の勤怠ステータスを取得
	public AttendanceDto getStatus(String userId) {
		LocalDate today = LocalDate.now();
		Optional<Attendance> attendanceOpt = attendanceRepository.findByUserIdAndWorkDate(userId, today);

		if (attendanceOpt.isEmpty()) {
			return new AttendanceDto("未出勤です", true, false);
		}

		Attendance attendance = attendanceOpt.get();
		// 日付部分の文字列を作成 (例: "7月7日")
		String dateStr = attendance.getWorkDate().format(dateFormatter);

		if (attendance.getClockOut() == null) {
			// 時間部分の文字列を作成 (例: "10時30分")
			String timeStr = attendance.getClockIn().format(timeFormatter);
			return new AttendanceDto("現在 " + dateStr + timeStr + "～出勤しています", false, true);
		} else {
			String timeStr = attendance.getClockOut().format(timeFormatter);
			return new AttendanceDto("現在 " + dateStr + timeStr + "に退勤しました", false, false);
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
		TempUserInfo user = userRepository.findById(userId)
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
		// 💡 【追加】現在の状態をチェックし、退勤できない状態ならエラー（例外）を投げる
		AttendanceDto currentStatus = this.getStatus(userId);
		if (!currentStatus.isCanClockOut()) {
			throw new IllegalArgumentException("本日の出勤データが見つからないか、すでに退勤しています");
		}

		// 1. 【最重要】本日・このユーザーの出勤レコードをDBから探す
		Attendance attendance = attendanceRepository.findByUserIdAndWorkDate(userId, LocalDate.now())
				.orElseThrow(() -> new RuntimeException("本日の出勤データが見つかりません。"));

		// 2. 見つかったレコードの clock_out に現在の退勤時刻をセットする
		attendance.setClockOut(LocalTime.now());

		// 3. 変更したレコードをDBに上書き保存（更新）する
		attendanceRepository.save(attendance);

		//		// --- 4. 【追加したログ登録処理】 ---
		//		Log clockOutLog = new Log();
		//		clockOutLog.setMessageId(1); // 1: {user_name}さんが退勤しました
		//		clockOutLog.setCreatedAt(LocalDateTime.now());
		//		clockOutLog.setTargetUserId(userId);
		//		logRepository.save(clockOutLog);

		// --- 4. ⭐共通メソッドへの置き換え ---
		// 今回はお使いの退勤用のメッセージID「1」を指定
		logService.saveLog(1, userId); // 👈 1行ですっきり！[cite: 1]

		// 5. 👈【追加】ユーザーの出退勤ステータスを「0: 退勤状態」に更新
		TempUserInfo user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("ユーザー情報が見つかりません。ID: " + userId));
		user.setAttendanceStatus(0); // ⭐ 0: 退勤状態
		userRepository.save(user);

		// --- 6. 戻り値処理 ---
		AttendanceDto dto = new AttendanceDto();
		dto.setStatusMessage("退勤しました");
		dto.setCanClockIn(false);
		dto.setCanClockOut(false);
		return dto;
	}
}
