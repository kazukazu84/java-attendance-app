package com.example.mypage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.mypage.entity.UserRoomStateEntity;

@Repository
public interface UserRoomStateRepository extends JpaRepository<UserRoomStateEntity, String> {}