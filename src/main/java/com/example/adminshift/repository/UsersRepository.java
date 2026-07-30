package com.example.adminshift.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.adminshift.entity.Users;

@Repository
public interface UsersRepository extends JpaRepository<Users, String> {
}