package com.example.salary.salaryconfirm.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.salary.salaryconfirm.dto.SalaryConfirmDto;
import com.example.salary.salaryconfirm.repository.SalaryConfirmRepository;
import com.example.salary.salarydetail.entity.SalaryEntity;

@Service
public class SalaryConfirmService {

    @Autowired
    private SalaryConfirmRepository salaryConfirmRepository;

    /**
     * 給与一覧（DTO）を取得
     */
    public List<SalaryConfirmDto> getSalaryList(int userId, int targetYear) {

        List<SalaryEntity> list =
                salaryConfirmRepository.findByUserIdAndTargetYear(userId, targetYear);

        List<SalaryConfirmDto> dtoList = new ArrayList<>();

        for (SalaryEntity s : list) {

            int appliedHourlyWage = s.getAppliedHourlyWage();

            // 総支給額 → 小数点以下不要なので整数化
            int grossSalary = (int) (s.getWorkingHours() * appliedHourlyWage);

            // 雇用保険料 → 小数点以下不要なので整数化
            int insuranceFee = s.isAppliedEmploymentInsurance()
                    ? (int) (grossSalary * 0.005)
                    : 0;

            // 差引支給額 → 小数点以下不要なので整数化
            int netSalary = grossSalary - insuranceFee;

            // ★ DTO は純粋なデータのみを保持（画面状態は Controller がセット）
            SalaryConfirmDto dto = new SalaryConfirmDto(
                    s.getTargetMonth(),
                    netSalary,
                    s.getUserId(),
                    s.getTargetYear()
            );

            dtoList.add(dto);
        }

        return dtoList;
    }

    /**
     * 年間勤務時間
     */
    public double getTotalWorkingHours(int userId, int targetYear) {

        return salaryConfirmRepository.findByUserIdAndTargetYear(userId, targetYear)
                .stream()
                .mapToDouble(SalaryEntity::getWorkingHours)
                .sum();
    }

    /**
     * 年間差引支給額
     */
    public double getTotalNetSalary(int userId, int targetYear) {

        List<SalaryEntity> list =
                salaryConfirmRepository.findByUserIdAndTargetYear(userId, targetYear);

        int total = 0;

        for (SalaryEntity s : list) {

            int appliedHourlyWage = s.getAppliedHourlyWage();

            int grossSalary = (int) (s.getWorkingHours() * appliedHourlyWage);

            int insuranceFee = s.isAppliedEmploymentInsurance()
                    ? (int) (grossSalary * 0.005)
                    : 0;

            int netSalary = grossSalary - insuranceFee;

            total += netSalary;
        }

        return total;
    }
}
