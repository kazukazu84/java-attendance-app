package com.example.salary.salarydetail.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.salary.salarydetail.entity.WageEntity;

@Repository
public interface WageRepository extends JpaRepository<WageEntity, Integer> {

    WageEntity findByWageId(int wageId);

}