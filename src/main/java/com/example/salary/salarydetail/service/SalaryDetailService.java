package com.example.salary.salarydetail.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.salary.salarydetail.dto.SalaryDetailDto;
import com.example.salary.salarydetail.entity.SalaryEntity;
import com.example.salary.salarydetail.repository.SalaryDetailRepository;

@Service
public class SalaryDetailService {

    @Autowired
    private SalaryDetailRepository salaryDetailRepository;

    public SalaryDetailDto getSalaryDetail(int userId, int targetYear, int targetMonth) {

        // ① salaryテーブルから該当年月の給与データを取得
        SalaryEntity salary = salaryDetailRepository
                .findByUserIdAndTargetYearAndTargetMonth(userId, targetYear, targetMonth);

        if (salary == null) {
            return null; // データなしの場合
        }

        // ② 時給は SalaryEntity の appliedHourlyWage を直接使用
        int appliedHourlyWage = salary.getAppliedHourlyWage();

        // ③ 総支給額（勤務時間 × 時給）→ 小数点以下不要なので整数化
        int grossSalary = (int) (salary.getWorkingHours() * appliedHourlyWage);

        // ④ 雇用保険料（適用時のみ）→ 小数点以下不要なので整数化
        int insuranceFee = salary.isAppliedEmploymentInsurance()
                ? (int) (grossSalary * 0.005)
                : 0;

        // ⑤ 差引支給額 → 小数点以下不要なので整数化
        int netSalary = grossSalary - insuranceFee;

        // ⑥ DTOに詰めて返却（画面状態は Controller がセットする）
        return new SalaryDetailDto(
                salary.getTargetYear(),
                salary.getTargetMonth(),
                salary.getWorkingHours(),
                appliedHourlyWage,
                grossSalary,
                insuranceFee,
                netSalary
        );
    }
}