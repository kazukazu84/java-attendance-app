package com.example.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.account.entity.UserInfo;

//UserInfoRepository.java
@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, String> {

}