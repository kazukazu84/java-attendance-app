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
     * 給与一覧（DTO）を取得
     */
    public List<SalaryConfirmDto> getSalaryList(String userId, int targetYear) {

        // ★ Repository から取得
        List<SalaryEntity> list =
                salaryConfirmRepository.findByUserInfoUserIdAndTargetYear(userId, targetYear);

        // ★ 月の降順（新しい月 → 古い月）にソート
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
