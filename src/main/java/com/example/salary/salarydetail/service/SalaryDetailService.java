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
     * 給与詳細取得（最新仕様に完全準拠）
     */
    public SalaryDetailDto getSalaryDetail(String userId, int targetYear, int targetMonth) {

        // ★ Optional から取り出す（最新仕様）
        SalaryEntity salary = salaryDetailRepository
                .findByUserInfoUserIdAndTargetYearAndTargetMonth(userId, targetYear, targetMonth)
                .orElse(null);

        if (salary == null) {
            return null; // データなし
        }

        // ★ SalaryEntity に保存済みの値をそのまま使用（再計算しない）
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
}
