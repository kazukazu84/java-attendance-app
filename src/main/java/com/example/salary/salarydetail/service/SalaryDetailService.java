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

    /**
     * 給与詳細取得（仕様に完全準拠）
     */
    public SalaryDetailDto getSalaryDetail(String userId, int targetYear, int targetMonth) {

        // ① salaryテーブルから該当年月の給与データを取得
        SalaryEntity salary = salaryDetailRepository
                .findByUserInfoUserIdAndTargetYearAndTargetMonth(userId, targetYear, targetMonth);

        if (salary == null) {
            return null; // データなし
        }

        // ② SalaryEntity に保存されている値をそのまま使用（再計算しない）
        int appliedHourlyWage = salary.getAppliedHourlyWage();
        double workingHours = salary.getWorkingHours();
        boolean appliedInsurance = salary.isAppliedEmploymentInsurance();

        // ③ 総支給額（保存済み）
        int grossSalary = salary.getGrossSalary();

        // ④ 雇用保険料（保存済み）
        int insuranceFee = salary.getInsuranceFee();

        // ⑤ 差引支給額（保存済み）
        int netSalary = salary.getNetSalary();

        // ⑥ DTOに詰めて返却
        return new SalaryDetailDto(
                salary.getTargetYear(),
                salary.getTargetMonth(),
                workingHours,
                appliedHourlyWage,
                grossSalary,
                insuranceFee,
                netSalary
        );
    }
}
