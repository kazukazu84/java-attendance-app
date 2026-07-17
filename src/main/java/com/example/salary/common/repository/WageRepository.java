package com.example.salary.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.salary.common.entity.WageEntity;

public interface WageRepository extends JpaRepository<WageEntity, Integer> {

    WageEntity findByWageId(int wageId);
}
