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
     * 勤怠確定時に給与を計算して salary に保存する（完全版）
     */
    public void calculateAndSaveMonthlySalary(String userId, int targetYear, int targetMonth) {

        // ① ユーザー情報取得
        UserInfo user = userInfoRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("ユーザーが存在しません: " + userId));

        // ② 時給マスタ取得
        Wage wage = wageRepository.findById(user.getWage().getWageId())
                .orElseThrow(() -> new IllegalArgumentException("時給マスタが存在しません: wage_id=" + user.getWage().getWageId()));

        int hourlyWage = wage.getWageValue();

        // ③ 月初〜月末の範囲で勤怠を取得（Between）
        LocalDate start = LocalDate.of(targetYear, targetMonth, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<Attendance> attendances =
                attendanceRepository.findByUserIdAndWorkDateBetween(userId, start, end);

        double totalWorkingHours = 0.0;

        for (Attendance att : attendances) {

            if (att.getClockIn() == null || att.getClockOut() == null) {
                continue;
            }

            long minutes = Duration.between(att.getClockIn(), att.getClockOut()).toMinutes();
            minutes -= att.getRestTime(); // 休憩時間を差し引く

            double hours = minutes / 60.0; // 15分刻みでも問題なし
            totalWorkingHours += hours;
        }

        // ④ 総支給額
        int grossSalary = (int) Math.round(totalWorkingHours * hourlyWage);

        // ⑤ 雇用保険料（0.3%）
        boolean appliedInsurance = user.isEmploymentInsurance();
        int insuranceFee = appliedInsurance ? (int) Math.round(grossSalary * 0.003) : 0;

        // ⑥ 差引支給額
        int netSalary = grossSalary - insuranceFee;

        // ⑦ salary テーブルに保存（履歴として保持）
        SalaryEntity salary = new SalaryEntity();
        salary.setUserInfo(user);
        salary.setTargetYear(targetYear);
        salary.setTargetMonth(targetMonth);
        salary.setWorkingHours(totalWorkingHours);
        salary.setAppliedHourlyWage(hourlyWage);
        salary.setAppliedEmploymentInsurance(appliedInsurance);
        salary.setGrossSalary(grossSalary);
        salary.setInsuranceFee(insuranceFee);
        salary.setNetSalary(netSalary);

        salaryDetailRepository.save(salary);
    }
}
