package com.example.salary.salaryconfirm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.salary.salarydetail.entity.SalaryEntity;

@Repository
public interface SalaryConfirmRepository extends JpaRepository<SalaryEntity, Integer> {

    List<SalaryEntity> findByUserIdAndTargetYear(int userId, int targetYear);

}