package com.example.salary.service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.account.entity.UserInfo;
import com.example.account.entity.Wage;
import com.example.account.repository.UserInfoRepository;
import com.example.account.repository.WageRepository;
import com.example.attendance.entity.Attendance;
import com.example.attendance.repository.AttendanceRepository;
import com.example.salary.salarydetail.entity.SalaryEntity;
import com.example.salary.salarydetail.repository.SalaryDetailRepository;

@Service
public class SalaryCalculationService {

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private WageRepository wageRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private SalaryDetailRepository salaryDetailRepository;

    /**
     * 勤怠確定時に給与を計算して salary に保存する（新規 or 更新）
     */
    public void calculateOrUpdateMonthlySalary(String userId, int targetYear, int targetMonth) {

        // ① ユーザー情報取得
        UserInfo user = userInfoRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("ユーザーが存在しません: " + userId));

        // ② 時給マスタ取得
        Wage wage = wageRepository.findById(user.getWage().getWageId())
                .orElseThrow(() -> new IllegalArgumentException("時給マスタが存在しません"));

        int hourlyWage = wage.getWageValue();

        // ③ 月初〜月末の範囲で勤怠を取得
        LocalDate start = LocalDate.of(targetYear, targetMonth, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<Attendance> attendances =
                attendanceRepository.findByUserIdAndWorkDateBetween(userId, start, end);

        double totalWorkingHours = 0.0;

        for (Attendance att : attendances) {

            if (att.getClockIn() == null || att.getClockOut() == null) continue;

            long minutes = Duration.between(att.getClockIn(), att.getClockOut()).toMinutes();
            minutes -= att.getRestTime();

            totalWorkingHours += (minutes / 60.0);
        }

        // ④ 総支給額
        int grossSalary = (int) Math.round(totalWorkingHours * hourlyWage);

        // ⑤ 雇用保険料
        boolean appliedInsurance = user.isEmploymentInsurance();
        int insuranceFee = appliedInsurance ? (int) Math.round(grossSalary * 0.003) : 0;

        // ⑥ 差引支給額
        int netSalary = grossSalary - insuranceFee;

        // ⑦ 既存 salary を検索
        SalaryEntity salary =
                salaryDetailRepository.findByUserInfoUserIdAndTargetYearAndTargetMonth(
                        userId, targetYear, targetMonth
                ).orElse(null);

        if (salary == null) {
            // ★ 新規作成
            salary = new SalaryEntity();
            salary.setUserInfo(user);
            salary.setTargetYear(targetYear);
            salary.setTargetMonth(targetMonth);
        }

        // ★ 新規でも更新でも共通の値をセット
        salary.setWorkingHours(totalWorkingHours);
        salary.setAppliedHourlyWage(hourlyWage);
        salary.setAppliedEmploymentInsurance(appliedInsurance);
        salary.setGrossSalary(grossSalary);
        salary.setInsuranceFee(insuranceFee);
        salary.setNetSalary(netSalary);

        salaryDetailRepository.save(salary);
    }
}
