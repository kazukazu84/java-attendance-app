package com.example.userShiftRequest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.userShiftRequest.entity.ShiftRequestDetailsEntity;

@Repository
public interface ShiftRequestDetailsRepository
        extends JpaRepository
        <ShiftRequestDetailsEntity, Integer> {
	
}