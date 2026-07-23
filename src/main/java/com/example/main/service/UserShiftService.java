package com.example.main.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.main.dto.UserShiftDto;

/*
 * ユーザーメイン画面のシフト表示を担当するサービスクラス
 * 
 * 【現在】シフト班のRepository完成待ち
 * 　　　　
 *  ShiftScheduleテーブルからログインユーザーのシフトを取得する
 */

@Service
public class UserShiftService {

	/*
	 * 月間シフト取得
	 * 
	 * ①ログインユーザーのuser_idを取得
	 * ②指定された年月のシフト一覧を取得する
	 * 例）2026年７月　→　7/1～7/31のシフト取得
	 * ③月間シフトカレンダーへ表示
	 * 
	 *  @param userId ログインユーザーID
	 *  @param yearMonth 表示対象年月
	 *  @return シフト一覧
	 */
    public List<UserShiftDto> getMonthlyShift(
            String userId,
            YearMonth yearMonth) {
    	
    	//ShiftScheduleRepository完成後に実装予定

        return null;
    }

    /*
     * 週間シフト取得
     * 
     * ①ログインユーザーのuser_idを取得
     * ②指定された週のシフト一覧を取得
     *  例）１週目　→　7/1～7/7のシフト取得
     * ③週間シフト一覧へ表示
     *  
     *  @param userId ログインユーザーID
     *  @param startDate 週開始日
     *  @param endDate 週終了日
     *  @return シフト一覧
     */
    public List<UserShiftDto> getWeeklyShift(
            String userId,
            LocalDate startDate,
            LocalDate endDate) {
    	
    	 // ShiftScheduleRepository完成後に実装予定

        return null;
    }
}