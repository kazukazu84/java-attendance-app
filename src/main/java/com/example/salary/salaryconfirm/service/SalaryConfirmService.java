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
    public List<SalaryConfirmDto> getSalaryList(String userId, int targetYear) {

        // ★ 最新仕様の Repository メソッド名に合わせる
        List<SalaryEntity> list =
                salaryConfirmRepository.findByUserInfoUserIdAndTargetYear(userId, targetYear);

        List<SalaryConfirmDto> dtoList = new ArrayList<>();

        for (SalaryEntity s : list) {

            // ★ 給与計算は保存済みの値をそのまま使用（再計算しない）
            int netSalary = s.getNetSalary();

            SalaryConfirmDto dto = new SalaryConfirmDto(
                    s.getTargetMonth(),
                    netSalary,
                    s.getUserInfo().getUserId(),   // ★ String に変更
                    s.getTargetYear()
            );

            dtoList.add(dto);
        }

        return dtoList;
    }

    /**
     * 年間勤務時間
     */
    public double getTotalWorkingHours(String userId, int targetYear) {

        return salaryConfirmRepository.findByUserInfoUserIdAndTargetYear(userId, targetYear)
                .stream()
                .mapToDouble(SalaryEntity::getWorkingHours)
                .sum();
    }

    /**
     * 年間差引支給額
     */
    public int getTotalNetSalary(String userId, int targetYear) {

        return salaryConfirmRepository.findByUserInfoUserIdAndTargetYear(userId, targetYear)
                .stream()
                .mapToInt(SalaryEntity::getNetSalary)
                .sum();
    }
}
