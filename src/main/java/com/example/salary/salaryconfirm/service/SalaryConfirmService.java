package com.example.salary.salaryconfirm.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
     * ★ 重複検知（自動修正はしない）
     * 同年同月の給与データが複数存在する場合は例外を投げる。
     */
    public void checkDuplicateSalary(String userId, int year, int month) {

        List<SalaryEntity> list =
                salaryConfirmRepository.findByUserInfoUserIdAndTargetYearAndTargetMonth(
                        userId, year, month
                );

        if (list.size() > 1) {
            throw new IllegalStateException(
                "同年同月の給与データが複数存在します。修正が必要です。"
            );
        }
    }

    /**
     * 給与一覧（DTO）を取得
     */
    public List<SalaryConfirmDto> getSalaryList(String userId, int targetYear) {

        List<SalaryEntity> list =
                salaryConfirmRepository.findByUserInfoUserIdAndTargetYear(userId, targetYear);

        list = list.stream()
                .sorted((a, b) -> Integer.compare(b.getTargetMonth(), a.getTargetMonth()))
                .collect(Collectors.toList());

        List<SalaryConfirmDto> dtoList = new ArrayList<>();

        for (SalaryEntity s : list) {

            int netSalary = s.getNetSalary();

            SalaryConfirmDto dto = new SalaryConfirmDto(
                    s.getTargetMonth(),
                    netSalary,
                    s.getUserInfo().getUserId(),
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

    public List<Integer> getAvailableYears(String userId) {
        return salaryConfirmRepository.findYearsByUserId(userId);
    }

}
