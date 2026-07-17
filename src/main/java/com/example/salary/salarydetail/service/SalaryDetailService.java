package com.example.salary.salarydetail.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.salary.salarydetail.dto.SalaryDetailDto;
import com.example.salary.salarydetail.entity.SalaryEntity;
import com.example.salary.salarydetail.entity.WageEntity;
import com.example.salary.salarydetail.repository.SalaryDetailRepository;
import com.example.salary.salarydetail.repository.WageRepository;

@Service
public class SalaryDetailService {

    @Autowired
    private SalaryDetailRepository salaryDetailRepository;

    @Autowired
    private WageRepository wageRepository;

    public SalaryDetailDto getSalaryDetail(int userId, int targetYear, int targetMonth) {

        // ① salaryテーブルから該当年月の給与データを取得
        SalaryEntity salary = salaryDetailRepository
                .findByUserIdAndTargetYearAndTargetMonth(userId, targetYear, targetMonth);

        if (salary == null) {
            return null; // データなしの場合
        }

        // ② wageテーブルから時給を取得
        WageEntity wage = wageRepository.findByWageId(salary.getWageId());
        int wageValue = wage.getWageValue();

        // ③ 総支給額（勤務時間 × 時給）
        double grossSalary = salary.getWorkingHours() * wageValue;

        // ④ 雇用保険料（適用時のみ）
        double employmentInsurance = salary.isAppliedEmploymentInsurance()
                ? grossSalary * 0.005
                : 0.0;

        // ⑤ 差引支給額
        double netSalary = grossSalary - employmentInsurance;

        // ⑥ DTOに詰めて返却
        return new SalaryDetailDto(
                salary.getWorkingHours(),
                wageValue,
                grossSalary,
                employmentInsurance,
                netSalary
        );
    }
}
