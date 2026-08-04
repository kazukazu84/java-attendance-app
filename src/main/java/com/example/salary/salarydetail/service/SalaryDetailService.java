package com.example.salary.salarydetail.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.attendance.entity.Attendance;
import com.example.attendance.repository.AttendanceRepository;
import com.example.salary.salarydetail.dto.SalaryDetailDto;
import com.example.salary.salarydetail.entity.SalaryEntity;
import com.example.salary.salarydetail.repository.SalaryDetailRepository;

@Service
public class SalaryDetailService {

    @Autowired
    private SalaryDetailRepository salaryDetailRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    /**
     * 給与詳細取得（最新仕様）
     * - 同年同月は必ず1件のみ
     * - 複数件存在したら例外を投げる（Controller が拾ってエラー画面へ）
     */
    public SalaryDetailDto getSalaryDetail(String userId, int targetYear, int targetMonth) {

        SalaryEntity salary = salaryDetailRepository
                .findByUserInfoUserIdAndTargetYearAndTargetMonth(userId, targetYear, targetMonth)
                .orElse(null);


        if (salary == null) {
            return null;
        }

        // ★ DB に保存されている値をそのまま返す（計算しない）
        return new SalaryDetailDto(
                salary.getTargetYear(),
                salary.getTargetMonth(),
                salary.getWorkingHours(),

        // ★ 0件なら空リスト
        if (salaryList.isEmpty()) {
            return List.of();
        }

        // ★ 複数件は不正 → Controller に投げる
        if (salaryList.size() > 1) {
            throw new IllegalStateException("同年同月の給与データが複数存在します。修正が必要です。");
        }

        // ★ 正常：1件だけ DTO に変換して返す
        SalaryEntity salary = salaryList.get(0);

        SalaryDetailDto dto = new SalaryDetailDto(
                salary.getTargetYear(),
                salary.getTargetMonth(),
                salary.getWorkingHours(),          // 時間（小数2桁）

                salary.getAppliedHourlyWage(),
                salary.getGrossSalary(),
                salary.getInsuranceFee(),
                salary.getNetSalary()
        );


        return List.of(dto);

    }

    /**
     * 勤怠一覧取得（Controller が DTO 化する）
     */
    public List<Attendance> getAttendanceList(String userId, int targetYear, int targetMonth) {
        return attendanceRepository.findByUserIdAndYearMonth(userId, targetYear, targetMonth);
    }
    
    public String getNullReason(String userId, int year, int month) {

        List<SalaryEntity> list =
                salaryDetailRepository.findByUserInfoUserIdAndTargetYearAndTargetMonth(
                        userId, year, month
                );

        if (list.isEmpty()) {
            return "該当月の給与データが存在しません。（" + year + "年" + month + "月）";
        }

        if (list.size() > 1) {
            return "同年同月の給与データが複数存在します。（重複エラー）";
        }

        return "給与データの取得に失敗しました。（予期しないエラー）";
    }

}
