package com.example.adminshift.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.adminshift.entity.ShiftSchedule;
import com.example.adminshift.entity.ShiftScheduleId;

@Repository
public interface ShiftScheduleRepository
        extends JpaRepository<ShiftSchedule, ShiftScheduleId> {

}
