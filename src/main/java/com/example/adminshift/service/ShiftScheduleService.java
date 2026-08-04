package com.example.adminshift.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.adminshift.entity.ShiftSchedule;
import com.example.adminshift.repository.ShiftScheduleRepository;

@Service
public class ShiftScheduleService {

    @Autowired
    private ShiftScheduleRepository repository;

    public List<ShiftSchedule> getShiftByUserIdAndDate(
            String userId,
            LocalDate startDate,
            LocalDate endDate) {

        return repository
                .findByIdUserIdAndIdWorkDateBetween(
                        userId,
                        startDate,
                        endDate);
    }
}