package com.example.salary.salaryconfirm.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.salary.salaryconfirm.dto.SalaryConfirmDto;
import com.example.salary.salaryconfirm.repository.SalaryConfirmRepository;
import com.example.salary.salarydetail.entity.SalaryEntity;
import com.example.salary.salarydetail.entity.WageEntity;
import com.example.salary.salarydetail.repository.WageRepository;

@Service
public class SalaryConfirmService {

    @Autowired
    private SalaryConfirmRepository salaryConfirmRepository;

    @Autowired
    private WageRepository wageRepository;

    /**
     * 給与一覧（DTO）を取得
     */
    public List<SalaryConfirmDto> getSalaryList(int userId, int targetYear) {

        List<SalaryEntity> list =
                salaryConfirmRepository.findByUserIdAndTargetYear(userId, targetYear);

        List<SalaryConfirmDto> dtoList = new ArrayList<>();

        for (SalaryEntity s : list) {

            // 時給取得
            WageEntity wage = wageRepository.findByWageId(s.getWageId());
            int wageValue = wage.getWageValue();

            // 総支給額
            double grossSalary = s.getWorkingHours() * wageValue;

            // 雇用保険料（boolean → 0.5%）
            double employmentInsurance = s.isAppliedEmploymentInsurance()
                    ? grossSalary * 0.005
                    : 0.0;

            // 差引支給額
            double netSalary = grossSalary - employmentInsurance;

            // DTOへ詰める（最小構成）
            SalaryConfirmDto dto = new SalaryConfirmDto(
                    s.getTargetMonth(),     // 対象月
                    (int) netSalary,        // 給与額（計算済み）
                    s.getUserId(),          // 詳細画面遷移用
                    s.getTargetYear()       // 詳細画面遷移用
            );

            dtoList.add(dto);
        }

        return dtoList;
    }

    /**
     * 年間勤務時間
     */
    public double getTotalWorkingHours(int userId, int targetYear) {

        double total = salaryConfirmRepository.findByUserIdAndTargetYear(userId, targetYear)
                .stream()
                .mapToDouble(SalaryEntity::getWorkingHours)
                .sum();

        return total;
    }

    /**
     * 年間差引支給額
     */
    public double getTotalNetSalary(int userId, int targetYear) {

        List<SalaryEntity> list =
                salaryConfirmRepository.findByUserIdAndTargetYear(userId, targetYear);

        double total = 0;

        for (SalaryEntity s : list) {

            WageEntity wage = wageRepository.findByWageId(s.getWageId());
            int wageValue = wage.getWageValue();

            double grossSalary = s.getWorkingHours() * wageValue;

            double employmentInsurance = s.isAppliedEmploymentInsurance()
                    ? grossSalary * 0.005
                    : 0.0;

            double netSalary = grossSalary - employmentInsurance;

            total += netSalary;
        }

        return total;
    }
}
