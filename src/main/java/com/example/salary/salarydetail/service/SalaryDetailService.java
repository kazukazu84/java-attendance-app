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
     * - 給与テーブルの値をそのまま返す
     * - 計算は SalaryCalculationService が担当
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
                salary.getAppliedHourlyWage(),
                salary.getGrossSalary(),
                salary.getInsuranceFee(),
                salary.getNetSalary()
        );
    }

    /**
     * 勤怠一覧取得（Controller が DTO 化する）
     */
    public List<Attendance> getAttendanceList(String userId, int targetYear, int targetMonth) {
        return attendanceRepository.findByUserIdAndYearMonth(userId, targetYear, targetMonth);
    }
}
