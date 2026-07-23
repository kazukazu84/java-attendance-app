package com.example.adminshift.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.adminshift.entity.ShiftRequestDetail;

@Repository
public interface ShiftRequestDetailRepository
        extends JpaRepository<ShiftRequestDetail, Integer> {

}