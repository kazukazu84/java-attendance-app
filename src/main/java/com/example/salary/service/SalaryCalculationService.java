package com.example.salary.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
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
     * ★ 重複自動修正
     * 同年同月の給与データが複数存在する場合、
     * 「最新の給与（salary_id が最大）」だけ残し、
     * 他は自動削除する。
     */
    private SalaryEntity resolveDuplicateSalary(String userId, int year, int month) {

        List<SalaryEntity> list = salaryDetailRepository
                .findByUserInfoUserIdAndTargetYearAndTargetMonth(userId, year, month);

        if (list.isEmpty()) {
            return null; // 新規作成
        }

        if (list.size() == 1) {
            return list.get(0); // 正常
        }

        // ★ 重複あり → 最新の salary_id を残す
        SalaryEntity latest = list.stream()
                .max((a, b) -> Integer.compare(a.getSalaryId(), b.getSalaryId()))
                .orElseThrow();

        // ★ 最新以外を削除
        for (SalaryEntity s : list) {
            if (s.getSalaryId() != latest.getSalaryId()) {
                salaryDetailRepository.delete(s);
            }
        }

        return latest;
    }

    /**
     * 勤怠確定時に給与を計算して salary に保存する（新規 or 更新）
     */
    public void calculateOrUpdateMonthlySalary(String userId, int targetYear, int targetMonth) {

        // ① ユーザー情報取得
        UserInfo user = userInfoRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("ユーザーが存在しません: " + userId));

        // ② 時給マスタ取得（最新値）
        Wage wage = wageRepository.findById(user.getWage().getWageId())
                .orElseThrow(() -> new IllegalArgumentException("時給マスタが存在しません"));

        int hourlyWage = wage.getWageValue();

        // ③ 今日の勤怠だけ取得（退勤直後）
        LocalDate today = LocalDate.now();
        Attendance att = attendanceRepository
                .findByUserIdAndWorkDate(userId, today)
                .orElse(null);

        if (att == null || att.getClockIn() == null || att.getClockOut() == null) {
            return; // 勤怠が揃っていない日はスキップ
        }

        // ★ 秒数以下切り捨て
        LocalTime in = att.getClockIn().withSecond(0);
        LocalTime out = att.getClockOut().withSecond(0);

        long minutes = Duration.between(in, out).toMinutes();

        Double restMinutes = att.getRestTime();
        if (restMinutes == null) restMinutes = 0.0;

        minutes -= restMinutes.intValue();
        if (minutes < 0) minutes = 0;

        // ★ 月給を取得（重複自動修正）
        SalaryEntity salary = resolveDuplicateSalary(userId, targetYear, targetMonth);

        if (salary == null) {
            salary = new SalaryEntity();
            salary.setUserInfo(user);
            salary.setTargetYear(targetYear);
            salary.setTargetMonth(targetMonth);
            salary.setWorkingHours(0);
            salary.setGrossSalary(0);
            salary.setInsuranceFee(0);
            salary.setNetSalary(0);
        }

        // ★ 勤務時間は加算方式（従来通り）
        double todayHours = minutes / 60.0;
        salary.setWorkingHours(salary.getWorkingHours() + todayHours);

        // ★ 月給を最新時給で再計算（workingHours を使う）
        double totalHours = salary.getWorkingHours();
        int totalMinutes = (int) Math.floor(totalHours * 60);

        int wagePerMinute = (int) Math.floor(hourlyWage / 60.0);
        int grossSalary = wagePerMinute * totalMinutes;

        boolean appliedInsurance = user.isEmploymentInsurance();
        int insuranceFee = appliedInsurance ? (int) Math.floor(grossSalary * 0.003) : 0;

        int netSalary = grossSalary - insuranceFee;

        // ★ 給与は最新値で上書き
        salary.setGrossSalary(grossSalary);
        salary.setInsuranceFee(insuranceFee);
        salary.setNetSalary(netSalary);

        // ★ 最新の時給・保険適用を保存
        salary.setAppliedHourlyWage(hourlyWage);
        salary.setAppliedEmploymentInsurance(appliedInsurance);

        salaryDetailRepository.save(salary);
    }

}