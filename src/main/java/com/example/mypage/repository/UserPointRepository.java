package com.example.mypage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.mypage.entity.UserPointEntity;

@Repository
public interface UserPointRepository extends JpaRepository<UserPointEntity, String> {}