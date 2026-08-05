package com.example.adminshift.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.adminshift.entity.ShiftRequestDetail;
import com.example.adminshift.repository.ShiftRequestDetailRepository;

@Service
public class ShiftRequestDetailService {

    @Autowired
    private ShiftRequestDetailRepository repository;

    public List<ShiftRequestDetail> getShiftByUserIdAndDate(
            String userId,
            LocalDate startDate,
            LocalDate endDate) {

        return repository
                .findByUserIdAndWorkDateBetweenOrderByWorkDateAsc(
                        userId,
                        startDate,
                        endDate);
    }
}