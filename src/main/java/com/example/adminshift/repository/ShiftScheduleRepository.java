package com.example.adminshift.repository;


import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.adminshift.entity.ShiftSchedule;
import com.example.adminshift.entity.ShiftScheduleId;

@Repository
public interface ShiftScheduleRepository
        extends JpaRepository<ShiftSchedule, ShiftScheduleId> {
	//新規追加（迫野）
	List<ShiftSchedule>
	findByIdUserIdAndIdWorkDateBetween(
	        String userId,
	        LocalDate startDate,
	        LocalDate endDate);

}
