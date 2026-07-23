package com.example.adminshift.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.adminshift.entity.ShiftApplicationSetting;

public interface ShiftApplicationSettingRepository
    extends JpaRepository<
        ShiftApplicationSetting,
        Integer> {

}


