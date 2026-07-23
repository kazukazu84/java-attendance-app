package com.example.salary.salarydetail.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.salary.salarydetail.entity.SalaryEntity;

@Repository
public interface SalaryDetailRepository extends JpaRepository<SalaryEntity, Integer> {

    SalaryEntity findByUserInfoUserIdAndTargetYearAndTargetMonth(
            String userId,
            int targetYear,
            int targetMonth
    );
}
