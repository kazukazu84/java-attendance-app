package com.example.mypage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.mypage.entity.UserAvatarEquipmentEntity;

@Repository
public interface UserAvatarEquipmentRepository extends JpaRepository<UserAvatarEquipmentEntity, String> {}