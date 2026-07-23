package com.example.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.main.entity.LogMessage;

@Repository
public interface LogMessageRepository extends JpaRepository<LogMessage, Integer> {
}