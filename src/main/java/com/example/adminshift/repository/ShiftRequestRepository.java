package com.example.adminshift.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.adminshift.entity.ShiftRequest;
import com.example.adminshift.entity.ShiftRequestId;

@Repository
public interface ShiftRequestRepository
        extends JpaRepository<ShiftRequest, ShiftRequestId> {

}