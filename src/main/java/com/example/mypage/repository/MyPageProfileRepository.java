package com.example.mypage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.mypage.entity.MyPageProfileEntity;

@Repository
public interface MyPageProfileRepository extends JpaRepository<MyPageProfileEntity, String> {}