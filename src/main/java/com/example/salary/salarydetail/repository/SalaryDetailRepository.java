package com.example.salary.salarydetail.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.salary.salarydetail.entity.SalaryEntity;

@Repository
public interface SalaryDetailRepository extends JpaRepository<SalaryEntity, Integer> {

    List<SalaryEntity> findByUserInfoUserIdAndTargetYearAndTargetMonth(
            String userId,
            int targetYear,
            int targetMonth
    );
}

