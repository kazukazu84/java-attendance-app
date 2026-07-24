package com.example.main.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.main.entity.ShiftScheduleTest;
import com.example.main.repository.ShiftScheduleTestRepository;

@Service
public class ShiftScheduleTestService {

    @Autowired
    private ShiftScheduleTestRepository repository;

    public List<ShiftScheduleTest> getAllShift() {

        return repository.findAll();
    }
    
    public List<ShiftScheduleTest> getShiftByWeek(
            LocalDate startDate,
            LocalDate endDate) {

        return repository.findByWorkDateBetween(
                startDate,
                endDate);
    }


}
