package com.example.main.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.main.entity.ShiftScheduleTest;

public interface ShiftScheduleTestRepository
        extends JpaRepository<ShiftScheduleTest, Long> {

    List<ShiftScheduleTest> findByWorkDateBetween(
            LocalDate startDate,
            LocalDate endDate);

}
