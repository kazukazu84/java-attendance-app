package com.example.adminshift.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.adminshift.entity.Shift;
import com.example.adminshift.repository.ShiftRepository;

@Service
public class ShiftService {

    @Autowired
    private ShiftRepository repository;

    public List<Shift> getShiftByUserIdAndDate(
            String userId,
            LocalDate startDate,
            LocalDate endDate) {

        return repository
                .findByUserIdAndShiftDateBetweenOrderByShiftDateAsc(
                        userId,
                        startDate,
                        endDate);
    }
}