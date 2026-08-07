package com.example.mypage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.mypage.entity.RoomItemEntity;

@Repository
public interface RoomItemRepository extends JpaRepository<RoomItemEntity, Long> {}